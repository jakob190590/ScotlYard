package kj.scotlyard.graphbuilder;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.SpinnerNumberModel;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JList;
import java.awt.event.ActionListener;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.graphbuilder.builder.Director;
import kj.scotlyard.graphbuilder.builder.GraphDescriptionBuilder;
import kj.scotlyard.graphbuilder.builder.ToolRepresentationBuilder;
import java.awt.event.KeyAdapter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings({ "serial", "rawtypes" })
public class BuilderTool extends JFrame {
	
	public static enum VertexType { STATION };
	public static enum EdgeType { TAXI_CONNECTION, BUS_CONNECTION, UNDERGROUND_CONNECTION, FERRY_CONNECTION };
	
	public static class Vertex {
		VertexType type;
		int number;
		Point2D.Double pos;
		public Vertex(VertexType type, int number, Point2D.Double pos) {
			this.type = type;
			this.number = number;
			this.pos = pos;
		}
		@Override
		public String toString() {
			return type.toString() + " #" + number;// + " " + pos;
		}
	}
	
	public static class Edge {
		EdgeType type;
		Vertex v1;
		Vertex v2;
		public Edge(EdgeType type, Vertex v1, Vertex v2) {
			this.type = type;
			this.v1 = v1;
			this.v2 = v2;
		}
		@Override
		public String toString() {
			return type.toString() + ": " + v1 + " - " + v2;
		}
	}
	
	private enum Tool { MARK_VERTEX, MARK_EDGE };
	
	private Tool currentTool = null;
	private Vertex lastSelectedVertex = null;
	private Image image = null;
	private Vector<Vertex> vertices = new Vector<>();
	private Vector<Edge> edges = new Vector<>();
	
	private Icon checkIcon = new ImageIcon("img/check.png");
	
	private File pwd = new File(".");
	private JFileChooser descriptionChooser = new JFileChooser();
	{
		descriptionChooser.setCurrentDirectory(pwd);
	}	
	private JFileChooser imageChooser = new JFileChooser();
	{
		imageChooser.setCurrentDirectory(pwd);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Images", "jpg", "gif", "jpeg", "png", "bmp"); // TODO was geht alles?
	    imageChooser.setFileFilter(filter);
	}

	private JPanel contentPane;
	private JToggleButton tglbtnMarkVertex;
	private JToggleButton tglbtnMarkEdge;
	private JComboBox cmbVertexType;
	private JComboBox cmbEdgeType;
	private JSpinner spinnerNumber;
	private JCheckBox cbPolyline;
	private JPanel imagePanel;
	private JSpinner spinnerSize;
	private JList<Vertex> vertexList;
	private JList<Edge> edgeList;
	private JLabel lblEdge;
	private final Action loadImage = new LoadImageAction();
	private final Action markVertex = new MarkVertexAction();
	private final Action markEdge = new MarkEdgeAction();
	private final Action importDescription = new ImportDescriptionAction();
	private final Action exportDescription = new ExportDescriptionAction();
	private final Action newGraph = new NewGraphAction();
	private final Action quitAction = new QuitAction();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuilderTool frame = new BuilderTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public BuilderTool() {
		setTitle("BuilderTool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 356);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadImage = new JMenuItem("");
		mntmLoadImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UNDEFINED, 0));
		mntmLoadImage.setAction(loadImage);
		mnFile.add(mntmLoadImage);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New menu item");
		mntmNewMenuItem_1.setAction(importDescription);
		mnFile.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("New menu item");
		mntmNewMenuItem_2.setAction(exportDescription);
		mnFile.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("New menu item");
		mntmNewMenuItem_3.setAction(quitAction);
		mnFile.add(mntmNewMenuItem_3);
		
		JMenu mnGraph = new JMenu("Graph");
		mnGraph.setMnemonic('g');
		menuBar.add(mnGraph);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mntmNewMenuItem.setAction(newGraph);
		mnGraph.add(mntmNewMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JToolBar toolBar = new JToolBar();
		panel.add(toolBar);
		
		tglbtnMarkVertex = new JToggleButton("Mark Vertex");
		tglbtnMarkVertex.setMnemonic('v');
		tglbtnMarkVertex.setAction(markVertex);
		toolBar.add(tglbtnMarkVertex);
		
		tglbtnMarkEdge = new JToggleButton("Mark Edge");
		tglbtnMarkEdge.setMnemonic('e');
		tglbtnMarkEdge.setAction(markEdge);
		toolBar.add(tglbtnMarkEdge);
		
		JLabel lblNodeSizepx = new JLabel("   Vertex size (px): ");
		lblNodeSizepx.setDisplayedMnemonic('s');
		toolBar.add(lblNodeSizepx);
		
		spinnerSize = new JSpinner();
		spinnerSize.setModel(new SpinnerNumberModel(new Integer(20), null, null, new Integer(1)));
		lblNodeSizepx.setLabelFor(spinnerSize);
		toolBar.add(spinnerSize);
		
		JToolBar toolBar_1 = new JToolBar();
		panel.add(toolBar_1);
		
		JLabel lblVertex = new JLabel("Vertex  ");
		lblVertex.setDisplayedMnemonic('t');
		lblVertex.setLabelFor(toolBar_1);
		toolBar_1.add(lblVertex);
		
		cmbVertexType = new JComboBox();
		cmbVertexType.setModel(new DefaultComboBoxModel(VertexType.values()));
		toolBar_1.add(cmbVertexType);
		
		JLabel lblNumber = new JLabel("   Number: ");
		lblNumber.setDisplayedMnemonic('n');
		toolBar_1.add(lblNumber);
		
		spinnerNumber = new JSpinner();
		lblNumber.setLabelFor(spinnerNumber);
		spinnerNumber.setModel(new SpinnerNumberModel(new Integer(1), null, null, new Integer(1)));
		toolBar_1.add(spinnerNumber);
		
		JToolBar toolBar_2 = new JToolBar();
		panel.add(toolBar_2);
		
		lblEdge = new JLabel("Edge  ");
		lblEdge.setDisplayedMnemonic('d');
		lblEdge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				endEdging();
			}
		});
		lblEdge.setLabelFor(toolBar_2);
		toolBar_2.add(lblEdge);
		
		cmbEdgeType = new JComboBox();
		cmbEdgeType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				endEdging();
			}
		});
		cmbEdgeType.setModel(new DefaultComboBoxModel(EdgeType.values()));
		toolBar_2.add(cmbEdgeType);
		
		cbPolyline = new JCheckBox("Polyline");
		cbPolyline.setMnemonic('p');
		cbPolyline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getCbPolyline().isSelected())
					endEdging();
			}
		});
		cbPolyline.setSelected(true);
		toolBar_2.add(cbPolyline);
		
		imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2D = (Graphics2D) g;
				if (image != null) {
					g2D.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				}
				
				g2D.setColor(Color.RED);
				g2D.setStroke(new BasicStroke(3));
				for (Vertex v : vertices) {
					int size = (int) getSpinnerSize().getValue();
					int radius = size / 2;
					int x = (int) Math.round(v.pos.x * getWidth());
					int y = (int) Math.round(v.pos.y * getHeight());
					g2D.drawOval(x - radius, y - radius, size, size);
				}
				
				g2D.setStroke(new BasicStroke(2));
				for (Edge e : edges) {
					Color cl = null;
					switch (e.type) {
					case TAXI_CONNECTION:
						cl = Color.YELLOW;
						break;
					case BUS_CONNECTION:
						cl = Color.GREEN;
						break;
					case UNDERGROUND_CONNECTION:
						cl = Color.BLUE;
						break;
					case FERRY_CONNECTION:
						cl = Color.BLACK;
						break;
					}
					g2D.setColor(cl);
					
					int x1 = (int) Math.round(e.v1.pos.x * getWidth());
					int y1 = (int) Math.round(e.v1.pos.y * getHeight());
					int x2 = (int) Math.round(e.v2.pos.x * getWidth());
					int y2 = (int) Math.round(e.v2.pos.y * getHeight());
					
					g2D.drawLine(x1, y1, x2, y2);
				}
			}
		};
		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				Vertex v = getVertex(new Point(e.getX(), e.getY()));

				double x = (double) e.getX() / getImagePanel().getWidth();
				double y = (double) e.getY() / getImagePanel().getHeight();
				Point2D.Double pos = new Point2D.Double(x, y);
				
				if (currentTool == Tool.MARK_VERTEX) {
					
					JSpinner number = getSpinnerNumber();				
					
					if (v == null) {
						v = new Vertex((VertexType) getCmbVertexType().getSelectedItem(), 
								(int) number.getValue(), pos); 
					
					} else {
						// schon vorhanden: nach vorne draengeln
						vertices.remove(v);
						v.number = (int) number.getValue();
					}
					
					// vorne einfuegen, weil vorne die aktuellsten sind.
					vertices.add(0, v);
					
					number.setValue(number.getNextValue());
					
				} else if (currentTool == Tool.MARK_EDGE) {
					
					if (lastSelectedVertex == null) {
						if (v != null) {
							lastSelectedVertex = v;
							startEdging();
						}
					} else if (v != null) {
						
						// connection zw. lastselected und v
						edges.add(0, new Edge((EdgeType) getCmbEdgeType().getSelectedItem(), lastSelectedVertex, v));
						
						if (getCbPolyline().isSelected()) {
							lastSelectedVertex = v;
						} else {
							endEdging();
						}
					}
				}
				
				updateUI();
				
			}
		});
		contentPane.add(imagePanel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.WEST);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblVertices = new JLabel("Vertices");
		GridBagConstraints gbc_lblVertices = new GridBagConstraints();
		gbc_lblVertices.insets = new Insets(0, 0, 5, 5);
		gbc_lblVertices.gridx = 0;
		gbc_lblVertices.gridy = 0;
		panel_1.add(lblVertices, gbc_lblVertices);
		
		JLabel lblEdges = new JLabel("Edges");
		GridBagConstraints gbc_lblEdges = new GridBagConstraints();
		gbc_lblEdges.insets = new Insets(0, 0, 5, 0);
		gbc_lblEdges.gridx = 1;
		gbc_lblEdges.gridy = 0;
		panel_1.add(lblEdges, gbc_lblEdges);
		
		vertexList = new JList<>();
		lblVertices.setLabelFor(vertexList);
		vertexList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					List<Vertex> selected = getVertexList().getSelectedValuesList();
					for (Vertex v : selected) {
						Iterator<Edge> it = edges.iterator();
						while (it.hasNext()) {
							Edge ee = it.next();
							if (ee.v1 == v || ee.v2 == v) {
								it.remove();
							}
						}
						vertices.remove(v);
					}
					updateUI();
				}
			}
		});
		GridBagConstraints gbc_vertexList = new GridBagConstraints();
		gbc_vertexList.weighty = 1.0;
		gbc_vertexList.insets = new Insets(0, 0, 0, 5);
		gbc_vertexList.fill = GridBagConstraints.BOTH;
		gbc_vertexList.gridx = 0;
		gbc_vertexList.gridy = 1;
		panel_1.add(vertexList, gbc_vertexList);
		
		edgeList = new JList<>();
		edgeList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					List<Edge> selected = getEdgeList().getSelectedValuesList();
					for (Edge ee : selected) {
						edges.remove(ee);
					}
					updateUI();
				}
			}
		});
		lblEdges.setLabelFor(edgeList);
		GridBagConstraints gbc_edgeList = new GridBagConstraints();
		gbc_edgeList.weighty = 1.0;
		gbc_edgeList.fill = GridBagConstraints.BOTH;
		gbc_edgeList.gridx = 1;
		gbc_edgeList.gridy = 1;
		panel_1.add(edgeList, gbc_edgeList);
		
	}

	protected JToggleButton getTglbtnMarkVertex() {
		return tglbtnMarkVertex;
	}
	protected JToggleButton getTglbtnMarkEdge() {
		return tglbtnMarkEdge;
	}
	protected JComboBox getCmbVertexType() {
		return cmbVertexType;
	}
	protected JComboBox getCmbEdgeType() {
		return cmbEdgeType;
	}
	protected JSpinner getSpinnerNumber() {
		return spinnerNumber;
	}
	protected JCheckBox getCbPolyline() {
		return cbPolyline;
	}
	protected JPanel getImagePanel() {
		return imagePanel;
	}
	protected JSpinner getSpinnerSize() {
		return spinnerSize;
	}
	protected JList getVertexList() {
		return vertexList;
	}
	protected JList getEdgeList() {
		return edgeList;
	}
	private Vertex getVertex(Point position) {
		
		double r = (int) getSpinnerSize().getValue() / 2.;
		
		for (Vertex v : vertices) {
			double diffx = v.pos.x * getImagePanel().getWidth() - position.x;
			double diffy = v.pos.y * getImagePanel().getHeight() - position.y;
			
			if (Math.sqrt(diffx * diffx + diffy * diffy) <= r) {
				return v;
			}
		}
		
		return null;
	}
	/** Festlegen der Kanten starten */
	void startEdging() {
		getLblEdge().setFont(new Font("Tahoma", Font.BOLD, 11));
		getLblEdge().setForeground(Color.BLUE);
		getLblEdge().setIcon(checkIcon);
	}
	/** Festlegen der Kanten beenden */
	void endEdging() {
		getLblEdge().setFont(new Font("Tahoma", Font.PLAIN, 11));
		getLblEdge().setForeground(Color.BLACK);
		getLblEdge().setIcon(null);
		lastSelectedVertex = null;
	}
	@SuppressWarnings("unchecked")
	protected void updateUI() {
		getImagePanel().repaint();
		
		getVertexList().setListData(vertices);
		getEdgeList().setListData(edges);
	}
	protected JLabel getLblEdge() {
		return lblEdge;
	}
	private class ImportDescriptionAction extends AbstractAction {
		public ImportDescriptionAction() {
			putValue(NAME, "Import Description...");
			putValue(SHORT_DESCRIPTION, "Import a Description file");
		}
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			if (descriptionChooser.showOpenDialog(BuilderTool.this) == JFileChooser.APPROVE_OPTION) {
				try {
					ToolRepresentationBuilder builder = new ToolRepresentationBuilder();
					Director.construct(descriptionChooser.getSelectedFile().getPath(), builder);
					vertices = new Vector(builder.getVertexList());
					edges = new Vector(builder.getEdgeList());
				} catch (IOException e1) {
					// TODO msg
					e1.printStackTrace();
				}
				updateUI();
			}
		}
	}
	private class ExportDescriptionAction extends AbstractAction {
		public ExportDescriptionAction() {
			putValue(NAME, "Export Description...");
			putValue(SHORT_DESCRIPTION, "Export a Description file");
		}
		public void actionPerformed(ActionEvent e) {
			if (descriptionChooser.showSaveDialog(BuilderTool.this) == JFileChooser.APPROVE_OPTION) {
				GraphDescriptionBuilder builder = new GraphDescriptionBuilder();
				for (Vertex v : vertices) {
					builder.addVertex(Station.class, v.number, v.pos);
				}
				for (Edge ee : edges) {
					Class<? extends ConnectionEdge> type = null;
					switch (ee.type) {
					case TAXI_CONNECTION:
						type = TaxiConnection.class;
						break;
					case BUS_CONNECTION:
						type = BusConnection.class;
						break;
					case UNDERGROUND_CONNECTION:
						type = UndergroundConnection.class;
						break;
					case FERRY_CONNECTION:
						type = FerryConnection.class;
						break;
					}
					builder.addEdge(type, ee.v1.number, ee.v2.number);
				}
				
				try {
					Writer w = new BufferedWriter(new FileWriter(descriptionChooser.getSelectedFile()));
					w.write(builder.getDescription());
					w.close();
				} catch (IOException e1) {
					// TODO msg
					e1.printStackTrace();
				}
			}
		}
	}
	private class NewGraphAction extends AbstractAction {
		public NewGraphAction() {
			putValue(NAME, "New Graph");
			putValue(SHORT_DESCRIPTION, "Clear the graph for a new one");
		}
		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(BuilderTool.this, "Really clear the current graph for a new one?") == JOptionPane.YES_OPTION) {
				vertices.clear();
				edges.clear();
				updateUI();
			}
		}
	}
	private class LoadImageAction extends AbstractAction {
		public LoadImageAction() {
			putValue(NAME, "Load Image...");
			putValue(SHORT_DESCRIPTION, "Load a background image");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl o"));
		}
		public void actionPerformed(ActionEvent e) {
			
		    if (imageChooser.showOpenDialog(BuilderTool.this) == JFileChooser.APPROVE_OPTION) {
		    	image = Toolkit.getDefaultToolkit().getImage(imageChooser.getSelectedFile().getPath());
		    	updateUI();
		    }
		}
	}
	private class MarkVertexAction extends AbstractAction {
		public MarkVertexAction() {
			putValue(NAME, "Mark Vertex");
			putValue(SHORT_DESCRIPTION, "Mark a vertex");
		}
		public void actionPerformed(ActionEvent e) {
			tglbtnMarkEdge.setSelected(false);
			endEdging();
			if (tglbtnMarkVertex.isSelected()) {
				currentTool = Tool.MARK_VERTEX;
			} else {
				currentTool = null;
			}
		}
	}
	private class MarkEdgeAction extends AbstractAction {
		public MarkEdgeAction() {
			putValue(NAME, "Mark Edge");
			putValue(SHORT_DESCRIPTION, "Mark an edge");
		}
		public void actionPerformed(ActionEvent e) {
			tglbtnMarkVertex.setSelected(false);
			if (tglbtnMarkEdge.isSelected()) {
				currentTool = Tool.MARK_EDGE;
				lastSelectedVertex = null;
			} else {
				currentTool = null;
				endEdging();
			}
		}
	}
	private class QuitAction extends AbstractAction {
		public QuitAction() {
			putValue(NAME, "Quit");
			putValue(SHORT_DESCRIPTION, "Quit");
		}
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	}
}
