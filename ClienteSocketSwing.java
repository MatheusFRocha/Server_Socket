package sockettrab;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClienteSocketSwing extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5261903818373181455L;
	private JTextArea taEditor = new JTextArea("");
	private JTextArea taVisor = new JTextArea();
	private JList liUsarios = new JList();
	private PrintWriter escritor;
	private BufferedReader leitor;
	private JScrollPane scrollTaVisor = new JScrollPane(taVisor);
	
	
	
	public ClienteSocketSwing() {
		
		
		
		setTitle("Chat com sockets - EDITADO");
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		liUsarios.setBackground(Color.GRAY);
		taEditor.setBackground(Color.WHITE);

		taEditor.setPreferredSize(new Dimension(400, 40));
//		taVisor.setPreferredSize(new Dimension(350, 100));
		taVisor.setEditable(false);
		liUsarios.setPreferredSize(new Dimension(100, 140));
		
		add(taEditor, BorderLayout.SOUTH);
		add(scrollTaVisor , BorderLayout.CENTER);
		add(new JScrollPane(liUsarios), BorderLayout.WEST);
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		String[] usuarios = new String[]{""};
		preencherListaUsuarios(usuarios);
		
	}
	
	
	/**
	 * Preenche a lista de usuarios
	 * @param usuarios
	 */
	private void preencherListaUsuarios(String[] usuarios) {
		DefaultListModel modelo = new DefaultListModel();
		liUsarios.setModel(modelo);
		for(String usuario: usuarios){
			modelo.addElement(usuario);			
		}
//		liUsarios.getModel().
		
	}


	private void iniciarEscritor() {
		taEditor.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
//				taEditor.getText()
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					// escrenvendo para o servidor
					if(taVisor.getText().isEmpty()){
						return;
					}
					
					Object usuario = liUsarios.getSelectedValue();
					
					if(usuario == null){
						if(taEditor.getText().equalsIgnoreCase(Comandos.SAIR)){
							System.exit(0);
						
						}
						
						else {
						JOptionPane.showMessageDialog(ClienteSocketSwing.this, "Selecione um usuario");
						return ;
						}}else{
						// jogando no visor
						taVisor.append("Eu: ");
						taVisor.append(taEditor.getText());
						taVisor.append("\n");
						
						escritor.println(Comandos.MENSAGEM + usuario);
						escritor.println(taEditor.getText());
						
						// limpando o editor
						taEditor.setText("");
						e.consume();}
					
					

				}				
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
	}
	
	public void iniciarChat() {
		try {
			final Socket cliente = new Socket("127.0.0.1", 9999);
			escritor = new PrintWriter(cliente.getOutputStream(), true);
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));			
		} catch (UnknownHostException e) {
			System.out.println("o endere�o passado � inv�lido");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("o servidor pode estar fora ar");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ClienteSocketSwing cliente = new ClienteSocketSwing();
		cliente.iniciarChat();
		cliente.iniciarEscritor();
		cliente.inicarLeitor();
	}
	
	private void atualizarListaUsuarios() {
		escritor.println(Comandos.LISTA_USUARIOS);
	}


	private void inicarLeitor() {
		// lendo mensagens do servidor
		try {
			while(true){
				String mensagem = leitor.readLine();
				if(mensagem == null || mensagem.isEmpty())
					continue;
				
				// recebe o texto
				if(mensagem.equals(Comandos.LISTA_USUARIOS)){
					String[] usuarios = leitor.readLine().split(",");
					preencherListaUsuarios(usuarios);
				}else if(mensagem.equals(Comandos.LOGIN)){
					String login = JOptionPane.showInputDialog("Qual o seu login?");
					escritor.println(login);
				}else if(mensagem.equals(Comandos.LOGIN_NEGADO)){
					JOptionPane.showMessageDialog(ClienteSocketSwing.this, "o login � inv�lido");
				}else if(mensagem.equals(Comandos.LOGIN_ACEITO)){
					atualizarListaUsuarios();	
				}else{
					taVisor.append(mensagem);
					taVisor.append("\n");
					taVisor.setCaretPosition(taVisor.getDocument().getLength());
				}
			}
			
		} catch (IOException e) {
			System.out.println("impossivel ler a mensagem do servidor");
			e.printStackTrace();
		}		
	}


	private DefaultListModel getListaUsuarios() {
		return (DefaultListModel) liUsarios.getModel();
	}
	
}