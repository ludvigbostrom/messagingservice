package Storage;

import model.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBAccessor extends DataStore{


    private Connection conn = null;
    private String jdbcUrl;
    private String username;
    private String password;

    public void openConnection() throws ClassNotFoundException, SQLException, IOException {


        setPropValues();
        Class.forName("org.postgresql.Driver");

        conn = DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Override
    public List<Message> getMessages(String receiver) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE receiver=?  order by timestamp");
        stmt.setString(1, receiver);
        ResultSet rs = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while (rs.next()) {
            messages.add(new Message(rs.getLong("msgId"), rs.getString("text"),
                    rs.getString("sender"), rs.getString("receiver"),
                    rs.getTimestamp("timestamp").toInstant().toEpochMilli(),
                    rs.getBoolean("isRead")));
        }
        rs.close();
        stmt.close();
        setRead(messages.stream().filter(m -> !m.isRead()).collect(Collectors.toList()));
        return messages;
    }


    @Override
    public List<Message> getUnreadMessages(String receiver) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE receiver=? AND isRead=false  order by timestamp");
        stmt.setString(1, receiver);
        ResultSet rs = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while (rs.next()) {
            messages.add(new Message(rs.getLong("msgId"), rs.getString("text"),
                    rs.getString("sender"), rs.getString("receiver"),
                    rs.getTimestamp("timestamp").toInstant().toEpochMilli(),
                    rs.getBoolean("isRead")));
        }
        rs.close();
        stmt.close();
        setRead(messages);
        return messages;
    }

    @Override
    public List<Message> getMessagesInRange(String receiver, long start, long stop) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE timestamp BETWEEN ? AND ?  order by timestamp");
        stmt.setTimestamp(1, new Timestamp(start));
        stmt.setTimestamp(2, new Timestamp(stop));
        ResultSet rs = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while (rs.next()) {
            messages.add(new Message(rs.getLong("msgId"), rs.getString("text"),
                    rs.getString("sender"), rs.getString("receiver"),
                    rs.getTimestamp("timestamp").toInstant().toEpochMilli(),
                    rs.getBoolean("isRead")));
        }
        rs.close();
        stmt.close();
        setRead(messages.stream().filter(m -> !m.isRead()).collect(Collectors.toList()));
        return messages;
    }

    @Override
    public Object getUnreadMessagesInRange(String username, long start, long stop) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE timestamp BETWEEN ? AND ? AND isread=false order by timestamp");
        stmt.setTimestamp(1, new Timestamp(start));
        stmt.setTimestamp(2, new Timestamp(stop));
        ResultSet rs = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while (rs.next()) {
            messages.add(new Message(rs.getLong("msgId"), rs.getString("text"),
                    rs.getString("sender"), rs.getString("receiver"),
                    rs.getTimestamp("timestamp").toInstant().toEpochMilli(),
                    rs.getBoolean("isRead")));
        }
        rs.close();
        stmt.close();
        setRead(messages.stream().filter(m -> !m.isRead()).collect(Collectors.toList()));
        return messages;
    }

    @Override
    public void addMessage(Message message) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages(timestamp, sender, receiver, text, isread) VALUES (?,?,?,?, ?);");
        stmt.setTimestamp(1, new Timestamp(message.getTimestamp()));
        stmt.setString(2, message.getSender());
        stmt.setString(3, message.getReceiver());
        stmt.setString(4, message.getText());
        stmt.setBoolean(5, message.isRead());
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void deleteMessage(long id) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("DELETE FROM messages WHERE msgId=?");
        stmt.setLong(1,id);
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void deleteMessages(List<Long> ids) throws SQLException {

        String sqlStatement  = "DELETE FROM messages WHERE msgId IN (";
        for(int i = 0; i < ids.size() ;i++) {
            sqlStatement += "?";
            if(i < ids.size()-1) {
                sqlStatement +=",";
            }
        }
        sqlStatement += ")";
        PreparedStatement stmt = conn.prepareStatement(sqlStatement);
        for(int i = 0; i < ids.size() ;i++) {
            stmt.setLong(i+1, ids.get(i));
        }

        stmt.executeUpdate();
        stmt.close();
    }

    private void setRead(List<Message> messages) throws SQLException {
        if(messages.size() == 0) {
            return;
        }
        String sqlStatement  = "UPDATE messages SET isread=true WHERE msgid in (";
        for(int i = 0; i < messages.size() ;i++) {
            sqlStatement += "?";
            if(i < messages.size()-1) {
                sqlStatement +=",";
            }
        }
        sqlStatement += ")";
        PreparedStatement stmt = conn.prepareStatement(sqlStatement);
        for(int i = 0; i < messages.size() ;i++) {
            stmt.setLong(i+1, messages.get(i).getId());
        }
        stmt.executeUpdate();
        stmt.close();
    }




    private void setPropValues() throws IOException {

        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        this.jdbcUrl = prop.getProperty("DB_URL");
        this.username = prop.getProperty("DB_USER");
        this.password = prop.getProperty("DB_PASS");
    }

}