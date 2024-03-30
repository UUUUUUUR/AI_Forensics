package ChatGPT;

import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

public class registry {

	private JFrame frame;
	private JTable table;
	
	public static DefaultTableModel model = new DefaultTableModel();
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					registry window = new registry();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public registry() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ArrayList<String> Valuelist = new ArrayList<String>();
		
		try {
			Process pro = Runtime.getRuntime().exec("cmd /c reg query \"HKEY_CURRENT_USER\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\MuiCache\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String str;
			while((str = br.readLine()) != null) {
				
				String[] spliststr = str.split("    ");
				if(spliststr.length > 3) {
					if(!str.contains("LangID    REG_BINARY    1204")) {
						String replaceStr = str.replace("\\", "\\\\");
						String[] result = replaceStr.split("    ");
						Valuelist.add(result[1]);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 723, 443);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		
		String[] header = {"Value", "위험도 판단", "부가 설명"};
        for (String h : header) {
            TableModelManager.getModel().addColumn(h);
        }
		
		
		
		table = new JTable(TableModelManager.getModel());
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn column = columnModel.getColumn(1); // 여기서 1은 두 번째 칼럼을 의미합니다. 
		column.setPreferredWidth(80); // 원하는 너비를 설정합니다.
		column.setMaxWidth(80); // 최대 너비를 설정합니다.
		column.setMinWidth(80); // 최소 너비를 설정합니다.
		scrollPane.setViewportView(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(12)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
					.addGap(12))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
					.addGap(10))
		);
		frame.getContentPane().setLayout(groupLayout);
		
		new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	ExecutorService executor = Executors.newFixedThreadPool(10);
				for (String question : Valuelist) {
				    Future<String[]> future = ModulesTest.main(question, executor);
				    
					try {
						String[] resultData = future.get();
						ImageIcon icon0 = new ImageIcon(registry.class.getResource("/img/0.png"));
						ImageIcon icon1 = new ImageIcon(registry.class.getResource("/img/1.png"));
						ImageIcon icon2 = new ImageIcon(registry.class.getResource("/img/2.png"));
						
						// resultData[1]의 값에 따라 이미지 선택
						ImageIcon icon;
						switch (resultData[1]) {
						    case "0":
						        icon = icon0;
						        break;
						    case "1":
						        icon = icon1;
						        break;
						    case "2":
						        icon = icon2;
						        break;
						    default:
						        icon = null; // 기본값 설정
						}
						TableModelManager.addRow(new Object[]{resultData[0], icon, resultData[2]});
						table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
						
					} catch (Exception e) {
						    e.printStackTrace();
					}
					
				}
		    	
		    	
		    }
		    }).start();
		
		
	}
}

class TableModelManager {
    private static DefaultTableModel model;

    static {
        model = new DefaultTableModel();
    }

    public static DefaultTableModel getModel() {
        return model;
    }

    public static void addRow(Object[] rowData) {
        model.addRow(rowData);
    }
}

class ImageRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value != null) {
            ImageIcon icon = (ImageIcon) value;
            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}





