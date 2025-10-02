package org.ashwin.service;

import org.ashwin.service.annotations.Column;
import org.ashwin.service.annotations.Entity;
import org.ashwin.service.annotations.Id;
import org.ashwin.service.annotations.Repository;
import org.ashwin.service.annotations.Autowired;
import org.ashwin.service.annotations.ComponentScan;
import org.ashwin.service.annotations.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private final Map<Class<?>, Object> beanRegistry = new HashMap<>();
    private final Map<Class<?>, EntityMeta> entityRegistry = new HashMap<>();

    public ApplicationContext(Class<?> mainClass){
        try {
            if (!mainClass.isAnnotationPresent(ComponentScan.class)){
                throw new RuntimeException("Main class must have @ComponentScan annotation");
            }

            String basePackage = mainClass.getAnnotation(ComponentScan.class).value();
            List<Class<?>> classes = scanPackage(basePackage);
            for (Class<?> clazz : classes) {
                if ( clazz.isAnnotationPresent(Entity.class)){
                    registerEntity(clazz);
                }
            }
            for (Class<?> clazz : classes) {
                registerBean(clazz);
            }
            injectDependencies();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Class<?>> scanPackage(String basePackage) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        String path = basePackage.replace(".", "/");
        URL root = Thread.currentThread().getContextClassLoader().getResource(path);
        if (root == null) throw new RuntimeException("Package not found: " + basePackage);

        File[] files = new File(root.getFile()).listFiles();
        if (files == null) return classes;

        for (File file: files){
            if ( file.isDirectory()) {
                classes.addAll(scanPackage(basePackage + "." + file.getName()));
            }else if (file.getName().endsWith(".class")){
                String className = basePackage + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }

    private void registerBean(Class<?> clazz) throws Exception {
        if (clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)) {
            Object instance;
            if (clazz.isAnnotationPresent(Repository.class)) {
                Repository repositoryAnn = clazz.getAnnotation(Repository.class);
                Class<?> entityClass = repositoryAnn.entity();
                EntityMeta meta = entityRegistry.get(entityClass);

                if (meta == null) throw new RuntimeException("Entity not found: " + entityClass);

                instance = Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            new Class<?>[] { clazz },
                            new RepositoryInvocationHandler(meta)
                        );
            }else {
                instance = clazz.getDeclaredConstructor().newInstance();
            }
            beanRegistry.put(clazz, instance);
        }
    }

    private void registerEntity(Class<?> clazz) {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.table();
        Field idField = null;
        List<Field> columns = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if ( field.isAnnotationPresent(Column.class)) {
                columns.add(field);
                if (field.isAnnotationPresent(Id.class)) {
                    idField = field;
                }
            }
        }
        entityRegistry.put(clazz, new EntityMeta(clazz,tableName, idField, columns));
    }

    private void injectDependencies() throws Exception {
        for (Object bean: beanRegistry.values()){
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);

                    for (Object candidate : beanRegistry.values()) {
                        if (field.getType().isAssignableFrom(candidate.getClass())){
                            field.set(bean, candidate);
                        }
                    }
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz){
        return clazz.cast(beanRegistry.get(clazz));
    }
}
