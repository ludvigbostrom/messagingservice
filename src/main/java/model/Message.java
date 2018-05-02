package model;

public class Message {
    private long timestamp;
    private String text;
    private String sender;
    private String receiver;
    private boolean read = false;
    private long id;

    public Message(String text, String sender, String receiver) {

        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = System.currentTimeMillis();

    }
    public Message(long id, String text, String sender, String receiver, long timestamp, boolean read) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.read = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public Message copy() {
        return new Message(id,text,sender,receiver,timestamp, read);
    }

    public void setId(long id) {
        this.id = id;
    }
}
