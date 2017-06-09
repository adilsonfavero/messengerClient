package Controller;

import View.LibreChatClient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import javax.swing.JPanel;

public class ChatClient
  implements Runnable
{
  private Socket socket = null;
  private Thread thread = null;
  private DataInputStream console = null;
  private DataOutputStream streamOut = null;
  private ChatClientThread client = null;
  private boolean logado;
  private LibreChatClient cliente;
  private String usuario;
  private String senha;
  
  public String getUsuario() {
    return usuario;
  }
  
  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }
  
  public String getSenha() {
    return senha;
  }
  
  public void setSenha(String senha) {
    this.senha = senha;
  }
  
  public boolean isLogado() {
    return logado;
  }
  
  public void setLogado(boolean logado) {
    this.logado = logado;
  }
  

  public ChatClient(String serverName, int serverPort, LibreChatClient client)
  {
    cliente = client;
    setLogado(false);
    System.out.println("Establishing connection. Please wait ...");
    try
    {
      socket = new Socket(serverName, serverPort);
      System.out.println("Connected: " + socket);
      start();
    }
    catch (UnknownHostException uhe)
    {
      System.out.println("Host unknown: " + uhe.getMessage());
    }
    catch (IOException ioe)
    {
      System.out.println("Unexpected exception: " + ioe.getMessage());
    }
  }
  
  public void run()
  {
    while (thread != null) {
      try
      {
        if(login())
        {
            cliente.setMessageLogin("Usuario Logado:" + this.getUsuario() );
            cliente.hideLoginPanel();
        }
        streamOut.writeUTF(console.readLine());
        streamOut.flush();
      }
      catch (IOException ioe)
      {
        System.out.println("Sending error: " + ioe.getMessage());
        stop();
      }
    }
  }
  
  public void handle(String msg) {
    if (msg.equals(".bye"))
    {
      System.out.println("Good bye. Press RETURN to exit ...");
      stop();
    }
    else {
      System.out.println(msg);
    }
  }
  
  public boolean login() {
    boolean retorno = false;
    System.out.println(getUsuario() + " - " + getSenha());
    if (this.getUsuario().equals("adilson"))
    {
      retorno = true;
    }
    return retorno;
  }
  




  public void start()
    throws IOException
  {
    console = new DataInputStream(System.in);
    streamOut = new DataOutputStream(socket.getOutputStream());
    if (thread == null)
    {
      client = new ChatClientThread(this, socket);
      thread = new Thread(this);
      thread.start();
    }
  }
  
  public void stop() { if (thread != null) {
      thread.stop();
      thread = null;
    }
    try {
      if (console != null) console.close();
      if (streamOut != null) streamOut.close();
      if (socket != null) socket.close();
    }
    catch (IOException ioe) {
      System.out.println("Error closing ..."); }
    client.close();
    client.stop();
  }
  
  public static void main(String[] args) {}
}
