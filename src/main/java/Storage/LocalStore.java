package Storage;

import model.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class LocalStore extends DataStore {
    private List<Message> messages = new ArrayList<>();

    private LongAdder idGenerator = new LongAdder();

    @Override
    public List<Message> getMessages(String receiver) throws SQLException {
        List<Message> messages = this.messages.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().equals(receiver))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(Message::copy)
                .collect(Collectors.toList());
        setRead(messages);
        return messages;
    }

    @Override
    public List<Message> getUnreadMessages(String receiver) throws SQLException {

        List<Message> messages = this.messages.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().equals(receiver) && !m.isRead())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(Message::copy)
                .collect(Collectors.toList());
        setRead(messages);
        return messages;
    }

    @Override
    public List<Message> getMessagesInRange(String receiver, long start, long stop) throws SQLException {

        List<Message> messages = this.messages.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().equals(receiver) && m.getTimestamp() >= start && m.getTimestamp() <= stop)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(Message::copy)
                .collect(Collectors.toList());
        setRead(messages);
        return messages;
    }

    @Override
    public Object getUnreadMessagesInRange(String receiver, long start, long stop) throws SQLException {
        List<Message> messages = this.messages.stream()
                .filter(m -> m.getReceiver() != null && !m.isRead() && m.getReceiver().equals(receiver) && m.getTimestamp() >= start && m.getTimestamp() <= stop)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(Message::copy)
                .collect(Collectors.toList());
        setRead(messages);
        return messages;
    }
    @Override
    public void addMessage(Message message) throws SQLException {
        idGenerator.increment();
        message.setId(idGenerator.longValue());
        messages.add(message);

    }

    @Override
    public void deleteMessage(long id) throws SQLException {
        messages.removeIf(m -> m.getId() == id);

    }

    @Override
    public void deleteMessages(List<Long> ids) throws SQLException {
        messages.removeIf(m -> ids.contains(m.getId()));
    }

    private void setRead(List<Message> readMessages) throws SQLException {
        this.messages.stream()
                .filter(m -> readMessages.stream().anyMatch(msg -> msg.getId() == m.getId()))
                .forEach(m -> m.setRead(true));
    }


}
