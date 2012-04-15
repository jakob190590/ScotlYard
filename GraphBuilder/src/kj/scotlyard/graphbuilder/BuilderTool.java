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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
	private final Action repaintAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getImagePanel().repaint();
			
		}
	};
	private final Action endEdgingAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			endEdging();
		}
	};
	private JCheckBoxMenuItem mntmMarkVertex;
	private JCheckBoxMenuItem mntmMarkEdge;
	private JCheckBoxMenuItem chckbxmntmOnlySelectedVertices;
	private JCheckBoxMenuItem chckbxmntmOnlySelectedEdges;

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
		setBounds(100, 100, 733, 240);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadImage = new JMenuItem("");
		mntmLoadImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UNDEFINED, 0));
		mntmLoadImage.setAction(loadImage);
		mntmLoadImage.addActionListener(endEdgingAction);
		mnFile.add(mntmLoadImage);
		
		mnFile.addSeparator();
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New menu item");
		mntmNewMenuItem_1.setAction(importDescription);
		mntmNewMenuItem_1.addActionListener(endEdgingAction);
		mnFile.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("New menu item");
		mntmNewMenuItem_2.setAction(exportDescription);
		mntmNewMenuItem_2.addActionListener(endEdgingAction);
		mnFile.add(mntmNewMenuItem_2);
		
		mnFile.addSeparator();
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("New menu item");
		mntmNewMenuItem_3.setAction(quitAction);
		mntmNewMenuItem_3.addActionListener(endEdgingAction);
		mnFile.add(mntmNewMenuItem_3);
		
		JMenu mnGraph = new JMenu("Graph");
		mnGraph.setMnemonic('g');
		menuBar.add(mnGraph);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mntmNewMenuItem.setAction(newGraph);
		mntmNewMenuItem.addActionListener(endEdgingAction);
		mnGraph.add(mntmNewMenuItem);
		
		mnGraph.addSeparator();
		
		mntmMarkVertex = new JCheckBoxMenuItem("New menu item");
		mntmMarkVertex.setAction(markVertex);
		mntmMarkVertex.addActionListener(endEdgingAction);
		mnGraph.add(mntmMarkVertex);
		
		mntmMarkEdge = new JCheckBoxMenuItem("New menu item");
		mntmMarkEdge.setAction(markEdge);
		mntmMarkEdge.addActionListener(endEdgingAction);
		mnGraph.add(mntmMarkEdge);
		
		JMenu mnView = new JMenu("View");
		mnView.setMnemonic('v');
		menuBar.add(mnView);
		
		chckbxmntmOnlySelectedVertices = new JCheckBoxMenuItem("Only Draw selected Vertices");
		chckbxmntmOnlySelectedVertices.addActionListener(repaintAction);
		mnView.add(chckbxmntmOnlySelectedVertices);
		
		chckbxmntmOnlySelectedEdges = new JCheckBoxMenuItem("Only Draw selected Edges");
		chckbxmntmOnlySelectedEdges.addActionListener(repaintAction);
		mnView.add(chckbxmntmOnlySelectedEdges);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('h');
		menuBar.add(mnHelp);
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
		toolBar.add(lblNodeSizepx);
		
		spinnerSize = new JSpinner();
		spinnerSize.setModel(new SpinnerNumberModel(new Integer(20), null, null, new Integer(1)));
		spinnerSize.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	        	getImagePanel().repaint();
	        }
	    });

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
		cmbVertexType.addActionListener(repaintAction);
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
		cmbEdgeType.addActionListener(endEdgingAction);
		cmbEdgeType.addActionListener(repaintAction);
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
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(200);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2D = (Graphics2D) g;
				if (image != null) {
					g2D.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				}
				
				g2D.setStroke(new BasicStroke(3));
				for (Vertex v : vertices) {
					if (getChckbxmntmOnlySelectedVertices().isSelected() && v.type != getCmbVertexType().getSelectedItem()) {
						continue;
					}
					
					int size = (int) getSpinnerSize().getValue();
					int radius = size / 2;
					int x = (int) Math.round(v.pos.x * getWidth());
					int y = (int) Math.round(v.pos.y * getHeight());
					
					g2D.setColor(Color.RED);
					g2D.drawOval(x - radius, y - radius, size, size);
					
					g2D.setColor(Color.BLACK);
					g2D.setFont(new Font("", Font.PLAIN, 12));
					g2D.drawString(String.valueOf(v.number), x - radius / 2, y + radius / 2);
				}
				
				g2D.setStroke(new BasicStroke(2));
				for (Edge e : edges) {
					Color cl = null;
					int offs = 0;
					switch (e.type) {
					case TAXI_CONNECTION:
						cl = Color.YELLOW;
						offs = 1;
						break;
					case BUS_CONNECTION:
						cl = Color.GREEN;
						offs = 3;
						break;
					case UNDERGROUND_CONNECTION:
						cl = Color.BLUE;
						offs = -1;
						break;
					case FERRY_CONNECTION:
						cl = Color.BLACK;
						offs = -3;
						break;
					}
					g2D.setColor(cl);
					
					if (getChckbxmntmOnlySelectedEdges().isSelected()) {
						if (e.type == getCmbEdgeType().getSelectedItem()) {
							offs = 0;
						} else {
							continue;
						}
					}
					
					int x1 = (int) Math.round(e.v1.pos.x * getWidth()) + offs;
					int y1 = (int) Math.round(e.v1.pos.y * getHeight()) + offs;
					int x2 = (int) Math.round(e.v2.pos.x * getWidth()) + offs;
					int y2 = (int) Math.round(e.v2.pos.y * getHeight()) + offs;
					
					g2D.drawLine(x1, y1, x2, y2);
				}
			}
		};
		splitPane.setRightComponent(imagePanel);
		
		JPanel panelSidebar = new JPanel();
		splitPane.setLeftComponent(panelSidebar);
		GridBagLayout gbl_panelSidebar = new GridBagLayout();
		gbl_panelSidebar.columnWidths = new int[]{0, 0, 0};
		gbl_panelSidebar.rowHeights = new int[]{0, 0, 0};
		gbl_panelSidebar.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panelSidebar.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelSidebar.setLayout(gbl_panelSidebar);
		
		JLabel lblVertices = new JLabel("Vertices");
		GridBagConstraints gbc_lblVertices = new GridBagConstraints();
		gbc_lblVertices.insets = new Insets(8, 0, 5, 5);
		gbc_lblVertices.gridx = 0;
		gbc_lblVertices.gridy = 0;
		panelSidebar.add(lblVertices, gbc_lblVertices);
		
		JLabel lblEdges = new JLabel("Edges");
		GridBagConstraints gbc_lblEdges = new GridBagConstraints();
		gbc_lblEdges.insets = new Insets(8, 0, 5, 0);
		gbc_lblEdges.gridx = 1;
		gbc_lblEdges.gridy = 0;
		panelSidebar.add(lblEdges, gbc_lblEdges);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panelSidebar.add(scrollPane, gbc_scrollPane);
		
		vertexList = new JList<>();
		scrollPane.setViewportView(vertexList);
		lblVertices.setLabelFor(vertexList);
		vertexList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				endEdging();
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
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 1;
		panelSidebar.add(scrollPane_1, gbc_scrollPane_1);
		
		edgeList = new JList<>();
		scrollPane_1.setViewportView(edgeList);
		edgeList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				endEdging();
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
		
		pack();
		
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
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("pressed F8"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_I);
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
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("pressed F9"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_X);
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
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed N"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
		}
		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(BuilderTool.this, "Really clear the current graph for a new one?") == JOptionPane.YES_OPTION) {
				vertices.clear();
				edges.clear();
				spinnerNumber.setValue(1);
				updateUI();
			}
		}
	}
	private class LoadImageAction extends AbstractAction {
		public LoadImageAction() {
			putValue(NAME, "Load Image...");
			putValue(SHORT_DESCRIPTION, "Load a background image");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl pressed O"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
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
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("pressed INSERT"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_V);
		}
		public void actionPerformed(ActionEvent e) {
			if (currentTool != Tool.MARK_VERTEX) {
				currentTool = Tool.MARK_VERTEX;
				getTglbtnMarkVertex().setSelected(true);
				getMntmMarkVertex().setSelected(true);
				
				getTglbtnMarkEdge().setSelected(false);
				getMntmMarkEdge().setSelected(false);
			} else {
				currentTool = null;
				getTglbtnMarkVertex().setSelected(false);
				getMntmMarkVertex().setSelected(false);
			}
		}
	}
	private class MarkEdgeAction extends AbstractAction {
		public MarkEdgeAction() {
			putValue(NAME, "Mark Edge");
			putValue(SHORT_DESCRIPTION, "Mark an edge");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl pressed E"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		}
		public void actionPerformed(ActionEvent e) {
			if (currentTool != Tool.MARK_EDGE) {
				currentTool = Tool.MARK_EDGE;
				lastSelectedVertex = null;
				getTglbtnMarkEdge().setSelected(true);
				getMntmMarkEdge().setSelected(true);
				
				getTglbtnMarkVertex().setSelected(false);
				getMntmMarkVertex().setSelected(false);
			} else {
				currentTool = null;
				endEdging();
				getTglbtnMarkEdge().setSelected(false);
				getMntmMarkEdge().setSelected(false);
			}
		}
	}
	private class QuitAction extends AbstractAction {
		public QuitAction() {
			putValue(NAME, "Quit");
			putValue(SHORT_DESCRIPTION, "Quit");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt pressed F4"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
		}
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	}
	protected JMenuItem getMntmMarkVertex() {
		return mntmMarkVertex;
	}
	protected JMenuItem getMntmMarkEdge() {
		return mntmMarkEdge;
	}
	
	protected JCheckBoxMenuItem getChckbxmntmOnlySelectedVertices() {
		return chckbxmntmOnlySelectedVertices;
	}
	protected JCheckBoxMenuItem getChckbxmntmOnlySelectedEdges() {
		return chckbxmntmOnlySelectedEdges;
	}
}
