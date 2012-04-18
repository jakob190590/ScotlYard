package kj.scotlyard.graphbuilder;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JComponent;
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
import javax.swing.Timer;

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
import kj.scotlyard.graphbuilder.builder.ToolGraphBuilder;
import java.awt.event.KeyAdapter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

@SuppressWarnings({ "serial", "rawtypes" })
public class BuilderTool extends JFrame {

	private static final double ZOOM_FACTOR = 1.2;
	
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
			return type.toString() + " #" + number;// + ": " + pos.x + " " + pos.y;
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
	
	/** Fuer das Zeichnen der Edges */ 
	private Vertex lastSelectedVertex = null;
	private int clicksWithoutTool = 0;
	private Image image = null;
	private int imageLeft;
	private int imageTop;
	private int imageWidth;
	private int imageHeight;
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
		        "Images", "jpg", "gif", "jpeg", "png");
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
	private JScrollPane scrollPaneImage;
	private JSpinner spinnerSize;
	private JList<Vertex> vertexList;
	private JList<Edge> edgeList;
	private JLabel lblEdge;
	private JCheckBoxMenuItem mntmMarkVertex;
	private JCheckBoxMenuItem mntmMarkEdge;
	private JCheckBoxMenuItem chckbxmntmOnlySelectedVertices;
	private JCheckBoxMenuItem chckbxmntmOnlySelectedEdges;
	private final Action preserveImageAspectRationAction = new PreserveImageAspectRationAction();
	private final Action fitImageAction = new FitImageAction();
	private final Action zoomInAction = new ZoomInAction();
	private final Action zoomOutAction = new ZoomOutAction();
	private final Action loadImageAction = new LoadImageAction();
	private final Action markVertexAction = new MarkVertexAction();
	private final Action markEdgeAction = new MarkEdgeAction();
	private final Action importDescriptionAction = new ImportDescriptionAction();
	private final Action exportDescriptionAction = new ExportDescriptionAction();
	private final Action newGraphAction = new NewGraphAction();
	private final Action quitAction = new QuitAction();
	private final Action repaintAction = new RepaintAction();
	private final Action endEdgingAction = new EndEdgingAction();

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
		mntmLoadImage.setAction(loadImageAction);
		mnFile.add(mntmLoadImage);
		
		mnFile.addSeparator();
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New menu item");
		mntmNewMenuItem_1.setAction(importDescriptionAction);
		mnFile.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("New menu item");
		mntmNewMenuItem_2.setAction(exportDescriptionAction);
		mnFile.add(mntmNewMenuItem_2);
		
		mnFile.addSeparator();
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("New menu item");
		mntmNewMenuItem_3.setAction(quitAction);
		mnFile.add(mntmNewMenuItem_3);
		
		JMenu mnGraph = new JMenu("Graph");
		mnGraph.setMnemonic('g');
		menuBar.add(mnGraph);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mntmNewMenuItem.setAction(newGraphAction);
		mnGraph.add(mntmNewMenuItem);
		
		mnGraph.addSeparator();
		
		mntmMarkVertex = new JCheckBoxMenuItem("New menu item");
		mntmMarkVertex.setAction(markVertexAction);
		mnGraph.add(mntmMarkVertex);
		
		mntmMarkEdge = new JCheckBoxMenuItem("New menu item");
		mntmMarkEdge.setAction(markEdgeAction);
		mnGraph.add(mntmMarkEdge);
		
		JMenu mnView = new JMenu("View");
		mnView.setMnemonic('w');
		menuBar.add(mnView);
		
		chckbxmntmOnlySelectedVertices = new JCheckBoxMenuItem("Only selected Vertices");
		chckbxmntmOnlySelectedVertices.setToolTipText("Only draw vertices of the selected type");
		chckbxmntmOnlySelectedVertices.setMnemonic('v');
		chckbxmntmOnlySelectedVertices.addActionListener(repaintAction);
		
		
		JCheckBoxMenuItem chckbxmntmFit = new JCheckBoxMenuItem("Fit");
		chckbxmntmFit.setAction(fitImageAction);
		mnView.add(chckbxmntmFit);
		
		JCheckBoxMenuItem chckbxmntmAspect = new JCheckBoxMenuItem("Aspect");
		chckbxmntmAspect.setAction(preserveImageAspectRationAction);
		mnView.add(chckbxmntmAspect);
		
		JMenuItem mntmZoomIn = new JMenuItem("Zoom in");
		mntmZoomIn.setAction(zoomInAction);
		mnView.add(mntmZoomIn);
		
		JMenuItem mntmZoomOut = new JMenuItem("Zoom out");
		mntmZoomOut.setAction(zoomOutAction);
		mnView.add(mntmZoomOut);
		
		
		mnView.addSeparator();
				
		mnView.add(chckbxmntmOnlySelectedVertices);		
		
		chckbxmntmOnlySelectedEdges = new JCheckBoxMenuItem("Only selected Edges");
		chckbxmntmOnlySelectedEdges.setToolTipText("Only draw edges of the selected type");
		chckbxmntmOnlySelectedEdges.setMnemonic('e');
		chckbxmntmOnlySelectedEdges.addActionListener(repaintAction);
		mnView.add(chckbxmntmOnlySelectedEdges);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('h');
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About...");
		mntmAbout.setMnemonic('a');
		mntmAbout.setToolTipText("About this Tool");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				endEdging();
				JOptionPane.showMessageDialog(BuilderTool.this,
						"BuilderTool\nCopyright 2012 Jakob Schöttl\n\n" +
						"With this BuilderTool, you can create and edit\n" +
						"Graph-Toolkit-independent Description files for graphs.\n\n" +
						"Load an image as background, then mark the vertices\n" +
						"and the edges. For both you can specify the type.\n" +
						"You can also remove vertices and edges:\n" +
						"Select them in the lists and press DELETE.", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(mntmAbout);
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
		tglbtnMarkVertex.setAction(markVertexAction);
		toolBar.add(tglbtnMarkVertex);
		
		tglbtnMarkEdge = new JToggleButton("Mark Edge");
		tglbtnMarkEdge.setMnemonic('e');
		tglbtnMarkEdge.setAction(markEdgeAction);
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
		cmbVertexType.addActionListener(endEdgingAction);
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
		lblEdge.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "endEdging");
		lblEdge.getActionMap().put("endEdging", endEdgingAction);
		lblEdge.setLabelFor(toolBar_2);
		toolBar_2.add(lblEdge);
		
		cmbEdgeType = new JComboBox();
		cmbEdgeType.addActionListener(repaintAction);
		cmbEdgeType.addActionListener(endEdgingAction);
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
		
		scrollPaneImage = new JScrollPane();
		final int scrollBarUnitIncrement = 8;
		scrollPaneImage.getVerticalScrollBar().setUnitIncrement(scrollBarUnitIncrement);
		scrollPaneImage.getHorizontalScrollBar().setUnitIncrement(scrollBarUnitIncrement);
		splitPane.setRightComponent(scrollPaneImage);
		
		imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2D = (Graphics2D) g;
				
				imageLeft = 0; // left
				imageTop = 0; // top
				imageWidth = getWidth();
				imageHeight = getHeight();
				
				if (image != null) {
					if (isSelected(preserveImageAspectRationAction)) {

						imageWidth = image.getWidth(null);
						imageHeight = image.getHeight(null);
						double ar = (double) imageWidth / imageHeight; // aspect ratio
						
						int wp = getWidth();
						int hp = getHeight();
						
						if (hp * ar > wp) { 
							// -> auf breite anpassen
							imageWidth = wp;
							imageHeight = (int) (wp / ar);
							imageTop = (hp - imageHeight) / 2;
						} else {
							// -> auf hoehe anpassen
							imageWidth = (int) (hp * ar);
							imageHeight = hp;
							imageLeft = (wp - imageWidth) / 2;
						}
						g2D.drawImage(image, imageLeft, imageTop, imageWidth, imageHeight, this);
					} else {
						g2D.drawImage(image, 0, 0, getWidth(), getHeight(), this);
					}
				}
				
				g2D.setStroke(new BasicStroke(3));
				for (Vertex v : vertices) {
					if (getChckbxmntmOnlySelectedVertices().isSelected() && v.type != getCmbVertexType().getSelectedItem()) {
						continue;
					}
					
					int size = (int) getSpinnerSize().getValue();
					int radius = size / 2;
					int x = (int) Math.round(v.pos.x * imageWidth) + imageLeft;
					int y = (int) Math.round(v.pos.y * imageHeight) + imageTop;
					
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
					
					int x1 = (int) Math.round(e.v1.pos.x * imageWidth) + imageLeft + offs;
					int y1 = (int) Math.round(e.v1.pos.y * imageHeight) + imageTop + offs;
					int x2 = (int) Math.round(e.v2.pos.x * imageWidth) + imageLeft + offs;
					int y2 = (int) Math.round(e.v2.pos.y * imageHeight) + imageTop + offs;
					
					g2D.drawLine(x1, y1, x2, y2);
				}
			}
		};
		scrollPaneImage.setViewportView(imagePanel);
		imagePanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				Vertex v = getVertex(new Point(e.getX(), e.getY()));

				double x = (double) (e.getX() - imageLeft) / imageWidth;
				double y = (double) (e.getY() - imageTop) / imageHeight;
				Point2D.Double pos = new Point2D.Double(x, y);
				
				if (isSelected(markVertexAction)) {
					
					JSpinner number = getSpinnerNumber();				
					
					if (getVertex((int) number.getValue()) == null) {
						// Kein Vertex mit aktueller Nummer vorhanden
						
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
					} else {
						JOptionPane.showMessageDialog(BuilderTool.this, "A vertex with the current number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
				} else if (isSelected(markEdgeAction)) {
					
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
				} else {
					clicksWithoutTool++;
					if (clicksWithoutTool >= 2) {
						JOptionPane.showMessageDialog(BuilderTool.this, "You should select a Tool. You can either mark vertices or edges.");
						clicksWithoutTool = 0;
					}
					Timer t = new Timer(5000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							clicksWithoutTool = 0;						
						}
					});
					t.setRepeats(false);
					t.start();
				}
				
				updateUI();
				
			} 
		});
		
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
	protected JLabel getLblEdge() {
		return lblEdge;
	}	
	protected JScrollPane getImageScrollPane() {
		return scrollPaneImage;
	}
	protected JCheckBoxMenuItem getChckbxmntmOnlySelectedVertices() {
		return chckbxmntmOnlySelectedVertices;
	}
	protected JCheckBoxMenuItem getChckbxmntmOnlySelectedEdges() {
		return chckbxmntmOnlySelectedEdges;
	}
	
	
	// Hilfsmethoden
	
	private Vertex getVertex(int number) {
		for (Vertex v : vertices) {
			if (v.number == number) {
				return v;
			}
		}
		
		return null;
	}
	/**
	 * Holt den Vertex, der an der angegebenen Stelle liegt
	 * aus der Liste.
	 * @param position direkt die Koordinaten des Mausereignisses am Panel.
	 * Egal ob das Bild vergroessert ist oder irgendwo Raender sind.
	 * Die Umrechnung findet hier drinnen statt.
	 * @return
	 */
	private Vertex getVertex(Point position) {
		
		double r = (int) getSpinnerSize().getValue() / 2.;
		
		for (Vertex v : vertices) {
			double diffx = v.pos.x * imageWidth + imageLeft - position.x;
			double diffy = v.pos.y * imageHeight + imageTop - position.y;
			
			if (Math.sqrt(diffx * diffx + diffy * diffy) <= r) {
				return v;
			}
		}
		
		return null;
	}
	
	public static void setSelected(Action action, boolean value) {
		action.putValue(Action.SELECTED_KEY, value);
	}
	public static boolean isSelected(Action action) {
		return (boolean) action.getValue(Action.SELECTED_KEY);
	}
	
	/** Festlegen der Kanten starten */
	void startEdging() {
		getLblEdge().setFont(new Font("Tahoma", Font.BOLD, 11));
		getLblEdge().setForeground(Color.BLUE);
		getLblEdge().setIcon(checkIcon);
		getLblEdge().setToolTipText("Finish Edge Drawing (Esc)");
	}
	/** Festlegen der Kanten beenden */
	void endEdging() {
		getLblEdge().setFont(new Font("Tahoma", Font.PLAIN, 11));
		getLblEdge().setForeground(Color.BLACK);
		getLblEdge().setIcon(null);
		getLblEdge().setToolTipText("");
		lastSelectedVertex = null;
	}
	
	/** Aktualisiert ImagePanel mit Graph und Vertex/Edge List. */
	@SuppressWarnings("unchecked")	
	protected void updateUI() {
		getImagePanel().repaint();
		
		getVertexList().setListData(vertices);
		getEdgeList().setListData(edges);
	}
	
	
	// Actions
	
	private class PreserveImageAspectRationAction extends AbstractAction {
		public PreserveImageAspectRationAction() {
			putValue(NAME, "Preserve aspect ration");
			putValue(SHORT_DESCRIPTION, "Preserve the image aspect ration");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
			setSelected(this, true);
		}
		public void actionPerformed(ActionEvent e) {
			getImagePanel().repaint();
		}
	}
	private class FitImageAction extends AbstractAction {
		public FitImageAction() {
			putValue(NAME, "Fit Image");
			putValue(SHORT_DESCRIPTION, "Fit the image to the window");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
			putValue(MNEMONIC_KEY, KeyEvent.VK_F);
			setSelected(this, true);
		}
		public void actionPerformed(ActionEvent e) {
			boolean fitImage = isSelected(this);
			zoomInAction.setEnabled(!fitImage);
			zoomOutAction.setEnabled(!fitImage);
			preserveImageAspectRationAction.setEnabled(fitImage);
			if (fitImage) {
				// pref size auf 0 setzen, damit is es immer angepasst an Viewport. 
				getImagePanel().setPreferredSize(new Dimension());	//getImageScrollPane().getViewport().getSize());
				// Wenn man's aber an Viewport anpassen wuerde, waere es fix,
				// je nach dem, wie gross der Viewport war zu dem Zeitpunkt.
				
				getImageScrollPane().revalidate();
			} else {
				setSelected(preserveImageAspectRationAction, true);
			} 
			getImagePanel().repaint();
		}
	}
	private class ZoomInAction extends AbstractAction {
		public ZoomInAction() {
			setEnabled(false); // weil standardmaessig Fit Image ausgewaehlt ist
			putValue(NAME, "Zoom In");
			putValue(SHORT_DESCRIPTION, "Zoom into the image");
			putValue(MNEMONIC_KEY, KeyEvent.VK_I);
			
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '+', KeyEvent.CTRL_DOWN_MASK)); // dont work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed +")); // dont work
			
			// KeyStroke.getKeyStroke('+', KeyEvent.CTRL_DOWN_MASK)
			// wird aufgefasst als getKeyStroke(int, int) -- wtf?? java castet einen char lieber automatisch als int, anstatt auto boxing zu Character.
			//  http://stackoverflow.com/questions/7931862/java-int-and-char
			// was werd ich wohl eher mit dem character '-' meinen? den character oder eine Zahl...
			
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK)); // (numpad) would work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK)); // (non-numpad) would work
			// dabei will ich doch, dass es einfach funktioniert, wenn ein + getippt wird. dafuer ist doch auch getKeyStroke("ctrl typed ...") da, oder? 
		}
		public void actionPerformed(ActionEvent e) {
			getImagePanel().setPreferredSize(new Dimension((int) (getImagePanel().getWidth() * ZOOM_FACTOR), (int) (getImagePanel().getHeight() * ZOOM_FACTOR)));
			getImagePanel().revalidate();
		}
	}
	private class ZoomOutAction extends AbstractAction {
		public ZoomOutAction() {
			setEnabled(false); // weil standardmaessig Fit Image ausgewaehlt ist
			putValue(NAME, "Zoom Out");
			putValue(SHORT_DESCRIPTION, "Zoom out of the image");
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '-', KeyEvent.CTRL_DOWN_MASK)); // dont work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed -")); // dont work
			
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK)); // (numpad) would work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK)); // (non-numpad) would work
		}
		public void actionPerformed(ActionEvent e) {
			getImagePanel().setPreferredSize(new Dimension((int) (getImagePanel().getWidth() / ZOOM_FACTOR), (int) (getImagePanel().getHeight() / ZOOM_FACTOR)));
			getImagePanel().revalidate();
		}
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
			endEdging();
			if (descriptionChooser.showOpenDialog(BuilderTool.this) == JFileChooser.APPROVE_OPTION) {
				try {
					ToolGraphBuilder builder = new ToolGraphBuilder();
					Director.construct(descriptionChooser.getSelectedFile().getPath(), builder);
					vertices = new Vector(builder.getVertexList());
					edges = new Vector(builder.getEdgeList());
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BuilderTool.this, e1.getLocalizedMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
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
			endEdging();
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
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BuilderTool.this, e1.getLocalizedMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
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
			endEdging();
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
			endEdging();
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
			setSelected(this, false);
		}
		public void actionPerformed(ActionEvent e) {
			endEdging();
			if (isSelected(this)) {
				setSelected(markEdgeAction, false);
			} else {
				clicksWithoutTool = 0;
			}
		}
	}
	private class MarkEdgeAction extends AbstractAction {
		public MarkEdgeAction() {
			putValue(NAME, "Mark Edge");
			putValue(SHORT_DESCRIPTION, "Mark an edge");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl pressed E"));
			putValue(MNEMONIC_KEY, KeyEvent.VK_E);
			setSelected(this, false);
		}
		public void actionPerformed(ActionEvent e) {
			endEdging();
			if (isSelected(this)) {
				setSelected(markVertexAction, false);
			} else {
				clicksWithoutTool = 0;
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
			endEdging();
			setVisible(false);
			dispose();
		}
	}
	private class RepaintAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			getImagePanel().repaint();			
		}
	};
	private class EndEdgingAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			endEdging();			
		}
	};
}
