package org.ashwin.service;

import org.ashwin.service.enums.GenerationType;
import org.ashwin.example1.DatabaseConfig;
import org.ashwin.service.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryInvocationHandler implements InvocationHandler {
    private final EntityMeta meta;

    public RepositoryInvocationHandler(EntityMeta meta) throws Exception {
        this.meta = meta;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws Exception {
        Connection conn = ConnectionFactory.getConnection(DatabaseConfig.class);
        String url = conn.getMetaData().getURL().toLowerCase();
        String dbType = detectDbType(url);

        StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS " + meta.getTableName() + " (");

        List<String> uniqueDefs = new ArrayList<>();
        List<String> uniqueComboDefs = new ArrayList<>();

        List<Field> columns = meta.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Field field = columns.get(i);
            Column column = field.getAnnotation(Column.class);

            sql.append(column.name()).append(" ").append(toSqlType(columns.get(i).getType(), dbType));

            if (columns.get(i).isAnnotationPresent(Id.class)){
                sql.append(" PRIMARY KEY");
                if (field.isAnnotationPresent(GeneratedId.class)) {
                    GeneratedId gid = field.getAnnotation(GeneratedId.class);
                    if ( gid.strategy() == GenerationType.AUTO ) {
                        if (dbType.equals("sqlite")) sql.append(" AUTOINCREMENT");
                    }
                }
            }

            if (column.unique()){
                uniqueDefs.add("UNIQUE(" + column.name() + ")");
            }

            if (column.uniqueCombo()){
                uniqueComboDefs.add(column.name());
            }
            if (i < columns.size() - 1){
                sql.append(", ");
            }
        }

        if (!uniqueComboDefs.isEmpty()){
            String cols = String.join(", ", uniqueComboDefs);
            uniqueDefs.add("UNIQUE(" + cols + ")");
        }

        if (!uniqueDefs.isEmpty()){
            sql.append(", ").append(String.join(", ", uniqueDefs));
        }

        sql.append(")");

        System.out.println(sql.toString());

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql.toString());
        }
    }

    private String toSqlType(Class<?> type, String dbType) {
        if (type == int.class || type == Integer.class) {
            if (dbType == "sqlite") return "INTEGER";

            return "INT";
        }
        if (type == long.class || type == Long.class) return "BIGINT";
        if (type == String.class) return "VARCHAR(255)";
        if (type == boolean.class || type == Boolean.class) {
            if (dbType.equals("sqlite")) return "BOOLEAN"; // SQLite treats as INTEGER 0/1
            return "BOOLEAN";
        }
        if (type == double.class || type == Double.class) return "DOUBLE";
        return "TEXT";
    }

    private String detectDbType(String url) {
        if (url.startsWith("jdbc:sqlite")) return "sqlite";
        if (url.startsWith("jdbc:mysql")) return "mysql";
        if (url.startsWith("jdbc:postgresql")) return "postgres";
        if (url.startsWith("jdbc:h2")) return "h2";
        return "generic";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection conn = ConnectionFactory.getConnection(DatabaseConfig.class);
        
        if (method.isAnnotationPresent(Query.class)){
            String sql = method.getAnnotation(Query.class).value();
            return executeQuery(sql, args, conn, method.getReturnType());
        } else if (method.isAnnotationPresent(Update.class)) {
            String sql = method.getAnnotation(Update.class).value();
            return executeUpdate(sql, args, conn);
        } else if ( method.isAnnotationPresent(Delete.class)) {
            String sql = method.getAnnotation(Delete.class).value();
            return executeUpdate(sql, args, conn);
        }

        String methodName = method.getName();

        if ( methodName.equals("save")){
            return save(args[0], conn);
        } else if (methodName.equals("findById")) {
            return findById(args[0], conn);
        } else if (methodName.startsWith("findBy")) {
            String column = methodName.substring("findBy".length());
            column = Character.toLowerCase(column.charAt(0)) + column.substring(1);
            return findByColumn(column, args[0], conn);
        } else if (methodName.equals("update")) {
            return update(args[0], conn);
        } else if (methodName.equals("delete")) {
            return delete(args[0], conn);
        } else if (methodName.equals("deleteById"))  {
            return deleteById(args[0], conn);
        }
        throw new UnsupportedOperationException("Method not supported: " + methodName);
    }

    private Object executeQuery(String sql, Object[] args, Connection conn, Class<?> returnType) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    stmt.setObject(i + 1, args[i]);
                }
            }
            ResultSet rs = stmt.executeQuery();

            if (returnType.equals(List.class)) {
                List<Object> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
                return results;
            } else {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    private Object executeUpdate(String sql, Object[] args, Connection conn) throws Exception {
        System.out.println("Executing SQL: " + sql);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    stmt.setObject(i + 1, args[i]);
                }
            }
            return stmt.executeUpdate();
        }
    }

    private Object mapRow(ResultSet rs) throws Exception {
        Object obj = meta.getEntityClass().getDeclaredConstructor().newInstance();
        for (Field field : meta.getColumns()) {
            field.setAccessible(true);
            field.set(obj, rs.getObject(field.getAnnotation(Column.class).name()));
        }
        return obj;
    }

    private Object save(Object entity, Connection conn) throws Exception {
        StringBuffer sql = new StringBuffer("INSERT INTO " + meta.getTableName() + " (");
        StringBuffer placeholders = new StringBuffer("VALUES (");
        List<Object> values = new ArrayList<>();

        boolean isGenerated = meta.getIdField().isAnnotationPresent(GeneratedId.class);
        GeneratedId gid = isGenerated ? meta.getIdField().getAnnotation(GeneratedId.class) : null;

        List<Field> columns = meta.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setAccessible(true);
            Column column = columns.get(i).getAnnotation(Column.class);


            if (columns.get(i).equals(meta.getIdField()) && isGenerated){
                if (gid.strategy() == GenerationType.AUTO) {
                    continue; // DB will generate → skip inserting id
                }

                if (gid.strategy() == GenerationType.CUSTOM) {
                    IdGenerator gen = gid.generator().getConstructor().newInstance();
                    Object idValue = gen.generate();
                    meta.getIdField().set(entity, idValue);
                }
            }

            sql.append(column.name());
            placeholders.append("?");
            values.add(columns.get(i).get(entity));

            if (i < columns.size() - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }
        }

        sql.append(") ").append(placeholders).append(")");

        System.out.println(sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();

            if (isGenerated && gid.strategy() == GenerationType.AUTO) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    meta.getIdField().set(entity, keys.getObject(1));
                }
            }
        }
        return entity;
    }

    private Object findById(Object id, Connection conn) throws Exception {
        String sql = "SELECT * FROM " + meta.getTableName() + " WHERE " +
                meta.getIdField().getAnnotation(Column.class).name() + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    private Object findByColumn(String column, Object value, Connection conn) throws Exception {
        String sql = "SELECT * FROM " + meta.getTableName() + " WHERE " + column + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            List<Object> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;
        }
    }

    private Object update(Object entity, Connection conn) throws Exception {
        StringBuilder sql = new StringBuilder("UPDATE " + meta.getTableName() + " SET ");
        List<Object> values = new ArrayList<>();

        Field idField = meta.getIdField();
        idField.setAccessible(true);
        Object idValue = idField.get(entity);
        if (idValue == null) {
            throw new RuntimeException("Cannot update entity without ID value");
        }

        List<Field> columns = meta.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Field field = columns.get(i);
            field.setAccessible(true);

            if (field.equals(idField)) continue; // skip ID in update

            Column column = field.getAnnotation(Column.class);
            sql.append(column.name()).append("=?");
            values.add(field.get(entity));

            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ")
                .append(idField.getAnnotation(Column.class).name())
                .append(" = ?");
        values.add(idValue);

        System.out.println(sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
        }
        return entity;
    }

    private Object delete(Object entity, Connection conn) throws Exception {
        Field idField = meta.getIdField();
        idField.setAccessible(true);
        Object idValue = idField.get(entity);
        if (idValue == null) {
            throw new RuntimeException("Cannot delete entity without ID value");
        }

        String sql = "DELETE FROM " + meta.getTableName() + " WHERE " +
                idField.getAnnotation(Column.class).name() + " = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, idValue);
            stmt.executeUpdate();
        }
        return null;
    }

    private Object deleteById(Object id, Connection conn) throws Exception {
        String sql = "DELETE FROM " + meta.getTableName() + " WHERE " +
                meta.getIdField().getAnnotation(Column.class).name() + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
        return null;
    }
}
