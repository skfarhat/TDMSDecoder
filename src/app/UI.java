package app;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.BasicConfigurator;

import tdms.Channel;
import tdms.TDMSManager;

/**
 * 
 * @author Sami
 *
 */
public class UI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8100996873581957242L;

	static {BasicConfigurator.configure();}
	
	static final int width 		= 400; 
	static final int height 	= 600; 
	
	private JPanel panel;
	DefaultTableModel model; 
	public UI() {
		super("TMDS Decoder");
		
		// pane
		panel = new JPanel(); 
		panel.setPreferredSize(new Dimension(width, height));
		panel.setLayout( new BorderLayout());;
		
		// table
		model 			= new DefaultTableModel(); 
		JTable table 	= new JTable(model);
		 

		JPanel buttonPanel 	= new JPanel();
		Button b1 			= new Button("Open"); 
		b1.addActionListener(e -> launchFileChooser());
		buttonPanel.add(b1); 
		
		panel.add(buttonPanel, BorderLayout.NORTH); 
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		// frame
		setContentPane(panel);
		setSize(new Dimension(400, 600));
		setVisible(true); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void launchFileChooser() { 
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("TDMS files (.tdms)", "tdms"));
		int returnVal = chooser.showOpenDialog(panel); 
		if (returnVal == JFileChooser.CANCEL_OPTION) {
			// do nothing
		} 
		else if (returnVal == JFileChooser.APPROVE_OPTION) {
			// read tdms file here
			// ..
			TDMSManager manager = new TDMSManager(); 
			try {
				final String filepath = chooser.getSelectedFile().getAbsolutePath();
				ArrayList<Channel> channels = new ArrayList<>(manager.read(filepath));
				for (Channel channel : channels) {
					String 			columnName = channel.getObjectPath(); 
					List<Object> 	columnData = channel.getData(); 
					model.addColumn(columnName, columnData.toArray());
				}
			} catch (Exception e) {
				// ALERT ON UI 
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		
		new UI(); 
		
	}
}
