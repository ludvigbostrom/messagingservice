package model;

public class MessageReq {



    private String text;
    private String sender;
    private String receiver;


    public MessageReq(){

    }

    public MessageReq(String text, String sender, String receiver) {

        this.text = text;
        this.sender = sender;
        this.receiver = receiver;

    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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


    public Message toMessage() {
        return new Message(text,sender,receiver);
    }

}
