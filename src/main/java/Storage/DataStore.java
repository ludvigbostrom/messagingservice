package Storage;

import model.Message;

import java.sql.SQLException;
import java.util.List;

public abstract class DataStore {
    public abstract List<Message> getMessages(String receiver) throws SQLException;

    public abstract List<Message> getUnreadMessages(String receiver) throws SQLException;

    public abstract List<Message> getMessagesInRange(String receiver, long start, long stop) throws SQLException;

    public abstract void addMessage(Message message) throws SQLException;

    public abstract void deleteMessage(long id) throws SQLException;

    public abstract void deleteMessages(List<Long> ids) throws SQLException;

    public abstract Object getUnreadMessagesInRange(String username, long fromTimestamp, long toTimestamp) throws SQLException;
}
