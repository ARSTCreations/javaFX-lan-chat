package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Main extends Application {

    public DatagramSocket dgssend;
    public DatagramSocket dgsrece;
    public DatagramPacket dgp;
    public String message;
    public InetAddress ip;
    public int port;

    @Override
    public void start(Stage primaryStage) throws IOException{
        dgssend = new DatagramSocket();
        dgsrece = new DatagramSocket(8888);
        dgp = null;
        message = null;

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 400, 375);
        primaryStage.setScene(scene);
        //"192.168.0.10"
        TextArea messageTa = new TextArea();
        TextArea composeTa = new TextArea();
        TextField ipTf = new TextField();
        TextField portTf = new TextField("8888");
        messageTa.setEditable(false);
        Button sendButton = new Button("SEND");
        grid.add(ipTf,0,0);
        grid.add(portTf,1,0);
        grid.add(messageTa,0,2,2,1);
        grid.add(composeTa,0,3,2,1);
        grid.add(sendButton,0,4,2,1);

        sendButton.setOnAction(send -> {
            try{
                message = composeTa.getText();
                ip = InetAddress.getByName(ipTf.getText());
                port = Integer.parseInt(portTf.getText());
                dgp = new DatagramPacket(message.getBytes(),message.getBytes().length,ip,port);
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                messageTa.appendText("I: " +format.format(date) + '\n');
                messageTa.appendText(message +" "+ '\n');
                dgssend.send(dgp);
                composeTa.setText("");
            }catch(Exception e)
            {e.printStackTrace();}
        });

        class readmessages implements Runnable {
            @Override
            public void run() {
                while(true) {
                    try{
                        byte[] buf = new byte[1024];
                        DatagramPacket dgpread = new DatagramPacket(buf,buf.length);
                        dgsrece.receive(dgpread);
                        String readmessage = new String(dgpread.getData(),0,dgpread.getLength());
                        Date date = new Date();
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        messageTa.appendText(dgpread.getAddress().toString()+": " +format.format(date) + '\n');
                        messageTa.appendText( readmessage + '\n');
                    }
                    catch(Exception e)
                    {e.getStackTrace();}
                }
            }
        }

        //ThreadStarter
        Thread thread = new Thread(new readmessages());
        thread.start();

        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}