package kj.scotlyard.game.rules;

import static org.junit.Assert.*;

import java.util.Map;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.ItemDealer;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

import org.junit.Before;
import org.junit.Test;

public class TheMovePolicyTest {

	SubMoves subMoves;
	
	Rules r;
	MovePolicy p;
	
	Game g;
	GameGraph gg;	
	Map<Integer, StationVertex> nsm;
	
	GameStateExtension ext;
	MrXPlayer mrX;
	DetectivePlayer d1, d2;
	
	Ticket tt, ut, bt, blt;
	DoubleMoveCard dmc;
	
	Station s1, s2, s3, s4;
	Connection tc1, uc1, bc1, fc1;
	Connection tc2, uc2, bc2, fc2;
	
	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		p = r.getMovePolicy();
		
		BoardGraphLoader loader = new BoardGraphLoader();
		loader.load("graph-description", "initial-stations");
		gg = loader.getGameGraph();
		nsm = loader.getNumberStationMap();
		
		tt = new TaxiTicket();
		ut = new UndergroundTicket();
		bt = new BusTicket();
		blt = new BlackTicket();
		
		dmc = new DoubleMoveCard();
		
		mrX = new MrXPlayer();
		d1 = new DetectivePlayer();
		d2 = new DetectivePlayer();
		
		g = new DefaultGame();
		ext = new GameStateExtension(g);
		g.setMrX(mrX);
		g.getDetectives().add(d1);
		g.getDetectives().add(d2);
		
		for (Player p : g.getPlayers()) {
			g.setItems(p, r.getGameInitPolicy().createItemSet(g, p));
		}
		
		s1 = new Station(gg);
		s2 = new Station(gg);
		s3 = new Station(gg);
		s4 = new Station(gg);
		
		tc1 = new TaxiConnection(gg);
		uc1 = new UndergroundConnection(gg);
		bc1 = new BusConnection(gg);
		fc1 = new FerryConnection(gg);
		tc2 = new TaxiConnection(gg);
		uc2 = new UndergroundConnection(gg);
		bc2 = new BusConnection(gg);
		fc2 = new FerryConnection(gg);

	}

	@Test
	public final void testIsTicketValidForConnection() {
		// pos tests
		assertTrue(p.isTicketValidForConnection(bt, bc1));
		assertTrue(p.isTicketValidForConnection(bt, bc2));
		assertTrue(p.isTicketValidForConnection(tt, tc1));
		assertTrue(p.isTicketValidForConnection(tt, tc2));
		assertTrue(p.isTicketValidForConnection(ut, uc1));
		assertTrue(p.isTicketValidForConnection(ut, uc2));
		assertTrue(p.isTicketValidForConnection(blt, fc1));
		assertTrue(p.isTicketValidForConnection(blt, fc2));
		
		assertTrue(p.isTicketValidForConnection(blt, bc1));
		assertTrue(p.isTicketValidForConnection(blt, bc2));
		assertTrue(p.isTicketValidForConnection(blt, uc1));
		assertTrue(p.isTicketValidForConnection(blt, uc2));
		assertTrue(p.isTicketValidForConnection(blt, tc1));
		assertTrue(p.isTicketValidForConnection(blt, tc2));
		
		// neg tests
		assertTrue(!p.isTicketValidForConnection(bt, fc1));
		assertTrue(!p.isTicketValidForConnection(bt, uc2));
		assertTrue(!p.isTicketValidForConnection(bt, tc2));
		
		assertTrue(!p.isTicketValidForConnection(tt, fc1));
		assertTrue(!p.isTicketValidForConnection(tt, bc2));
		assertTrue(!p.isTicketValidForConnection(tt, uc2));
		
		assertTrue(!p.isTicketValidForConnection(ut, fc1));
		assertTrue(!p.isTicketValidForConnection(ut, tc2));
		assertTrue(!p.isTicketValidForConnection(ut, bc2));
	}

	// das koennte ma auch in die extension einbauen.
	private Item getItem(Player player, Class<? extends Item> itemType) {
		for (Item item : g.getItems(player)) {
			if (item.getClass() == itemType) {
				return item;
			}
		}
		return null;
	}
	
	@Test
	public final void testCheckMove() {
		
		// in this test, all should simply work
		
		Move[][] ms = new Move[4][3];
		
		ms[0][0] = MoveProducer.createInitialMove(mrX, s2);
		ms[1][0] = MoveProducer.createSingleMove(mrX, 1, 1, s1, bc1, (Ticket) getItem(mrX, BusTicket.class));
		subMoves = new SubMoves()
				.add(s3, uc1, (Ticket) getItem(mrX, UndergroundTicket.class)) 
				.add(s4, tc1, (Ticket) getItem(mrX, TaxiTicket.class));  
		ms[2][0] = MoveProducer.createMultiMove(mrX, 2, 2, (DoubleMoveCard) getItem(mrX, DoubleMoveCard.class), subMoves);
		ms[3][0] = MoveProducer.createSingleMove(mrX, 3, 4, s1, uc1, (Ticket) getItem(mrX, UndergroundTicket.class));
		
		
		ms[0][1] = MoveProducer.createInitialMove(d1, s1);
		ms[1][1] = MoveProducer.createSingleMove(d1, 1, 1, s2, uc1, (Ticket) getItem(d1, UndergroundTicket.class));
		ms[2][1] = MoveProducer.createSingleMove(d1, 2, 2, s3, bc1, (Ticket) getItem(d1, BusTicket.class));  
		ms[3][1] = MoveProducer.createSingleMove(d1, 3, 3, s4, tc1, (Ticket) getItem(d1, TaxiTicket.class));
		
		
		ms[0][2] = MoveProducer.createInitialMove(d2, s1);
		ms[1][2] = MoveProducer.createSingleMove(d2, 1, 1, s2, tc1, (Ticket) getItem(d2, TaxiTicket.class));
		ms[2][2] = MoveProducer.createSingleMove(d2, 2, 2, s3, uc1, (Ticket) getItem(d2, UndergroundTicket.class));  
		ms[3][2] = MoveProducer.createSingleMove(d2, 3, 3, s4, bc1, (Ticket) getItem(d2, BusTicket.class));
		
		int i = 0; // runde
		for (Move[] ms1 : ms) {
			g.setCurrentRoundNumber(i);
			int j = 0;
			for (Move m : ms1) {
				g.setCurrentPlayer(g.getPlayers().get(j));
				p.checkMove(g, gg, m);
				g.getMoves().add(m);
				j++;
			}
			i++;
		}
		
	}

	@Test
	public final void testInitialMoveCheck() {
		// trying out initial moves
		for (int j = 0; j < 2; j++) {
			g.setCurrentRoundNumber(j);
			
			for (Player pl : g.getPlayers()) {
				g.setCurrentPlayer(pl);
				
				try {
					// player not part of game
					p.checkMove(g, gg, MoveProducer.createInitialMove(new MrXPlayer(), s1));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("not part of"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				if (pl != g.getPlayers().get(0)) {
					try {
						// not players turn
						p.checkMove(g, gg, MoveProducer.createInitialMove(g.getPlayers().get(0), s1));
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("turn"))
							fail("not the expected exception: " + e.getMessage());
					}
				}
				
				try {
					// missing station
					p.checkMove(g, gg, MoveProducer.createInitialMove(pl, j, null));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("station"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong round number
					p.checkMove(g, gg, MoveProducer.createInitialMove(pl, 2, s2));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("round number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass item in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.setItem(dmc);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("item"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass ticket in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.setItem(tt);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("item"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass connection in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.setConnection(bc1);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("connection"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass move index in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.setMoveIndex(0);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move index"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong move number in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.setMoveNumber(1);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass sub move in initial move
					Move m = MoveProducer.createInitialMove(pl, j, s3);
					m.getMoves().add(MoveProducer.createSingleMove(pl, 0, 0, s3, bc1, tt));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("multi move"))
						fail("not the expected exception: " + e.getMessage());
				}
			}
			
		}
	}
	
	@Test
	public final void testSingleMoveCheck() {
		g.setCurrentRoundNumber(0);
		
		for (Player pl : g.getPlayers()) {
			g.setCurrentPlayer(pl);
			Move m = MoveProducer.createInitialMove(pl, s1);
			p.checkMove(g, gg, m);
			g.getMoves().add(m);
		}
		
		for (int j = 1; j < 3; j++) {
			g.setCurrentRoundNumber(j);
			
			for (Player pl : g.getPlayers()) {
				g.setCurrentPlayer(pl);
				
				
				try {
					// player not part of game
					p.checkMove(g, gg, MoveProducer.createInitialMove(new MrXPlayer(), s1));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("not part of"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				if (pl != g.getPlayers().get(0)) {
					try {
						// not players turn
						p.checkMove(g, gg, MoveProducer.createInitialMove(g.getPlayers().get(0), s1));
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("turn"))
							fail("not the expected exception: " + e.getMessage());
					}
				}
				
				try {
					// invalid round number
					Move m = MoveProducer.createSingleMove(pl, j+3, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("round number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// invalid move number
					Move m = MoveProducer.createSingleMove(pl, j, j+3, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move number"))
						fail("not the expected exception: " + e.getMessage());
				}				
				
				try {
					// wrong move index
					Move m = MoveProducer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					m.setMoveIndex(0);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move index"))
						fail("not the expected exception: " + e.getMessage());
				}				
				
				try {
					// station null
					Move m = MoveProducer.createSingleMove(pl, j, j, null, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("station"))
						fail("not the expected exception: " + e.getMessage());
				}				
				
				try {
					// stolen ticket
					Move m = MoveProducer.createSingleMove(pl, j, j, s4, bc1, bt);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("stolen"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// invalid ticket
					Move m = MoveProducer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, TaxiTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("not valid"))
						fail("not the expected exception: " + e.getMessage());
				}				
				
				try {
					// not ticket but double move card
					Move m = MoveProducer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					m.setItem(dmc);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("provide a ticket"))
						fail("not the expected exception: " + e.getMessage());
				}				
				
				// sauberen move adden, damit der test in die naechste runde gehen kann
				g.getMoves().add(MoveProducer.createSingleMove(pl, j, j, s4, tc1, (Ticket) ext.getItem(pl, TaxiTicket.class)));
			}
		}		
	}
	
	@Test
	public final void testMultiMoveCheck() {
		g.setCurrentRoundNumber(0);
		
		for (Player pl : g.getPlayers()) {
			g.setCurrentPlayer(pl);
			Move m = MoveProducer.createInitialMove(pl, s1);
			p.checkMove(g, gg, m);
			g.getMoves().add(m);
		}
		
		
		for (int j = 1; j < 3; j++) {
			g.setCurrentRoundNumber(j);
			
			for (Player pl : g.getPlayers()) {
				g.setCurrentPlayer(pl);
		
				
				
				try {
					// player not part of game
					subMoves = new SubMoves()
							.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
							.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
					Move m = MoveProducer.createMultiMove(new MrXPlayer(), j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("not part of"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				if (pl != g.getDetectives().get(0)) {
					try {
						// not player's turn
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(g.getDetectives().get(0), j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("turn"))
							fail("not the expected exception: " + e.getMessage());
					}
				}
					
				if (pl instanceof DetectivePlayer) {
					try {
						// only mr x can
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(new MrXPlayer(), j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("is not part of"))
							fail("not the expected exception: " + e.getMessage());
					}
				}
				
				if (pl == g.getMrX()) {
					try {
						// players differs
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(1).setPlayer(new MrXPlayer());
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("differs from"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// round number differs
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(1).setRoundNumber(0);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("round number"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// wrong move index in sub move
						subMoves = new SubMoves()
						.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
						.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(1).setMoveIndex(3);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains(""))
							fail("not the expected exception: " + e.getMessage());
					}
					try {
						// wrong move number in sub move
						subMoves = new SubMoves()
						.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
						.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(1).setMoveNumber(40);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("move number"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// triple move forbidden
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("double move"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// komischer move forbidden
						subMoves = new SubMoves()
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("double move"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// further sub move forbidden
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(0).getMoves().add(MoveProducer.createInitialMove(new DetectivePlayer(), s4));
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("further sub moves"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// no connection in sub move
						subMoves = new SubMoves()
								.add(new Station(gg), null, (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("connection"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// a connection in base move
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.setConnection(tc2);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("connection"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// stolen ticket in sub move
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.getMoves().get(0).setItem(new TaxiTicket());
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("stolen"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// invalid ticket in sub move
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, UndergroundTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("not valid"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// no correct multi move card
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.setItem(new TaxiTicket());
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("double move card"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// no item in base move
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.setItem(null);
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("double move card"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					try {
						// stolen correct multi move card
						subMoves = new SubMoves()
								.add(new Station(gg), new TaxiConnection(gg), (Ticket) ext.getItem(pl, TaxiTicket.class))
								.add(new Station(gg), new BusConnection(gg), (Ticket) ext.getItem(pl, BusTicket.class));
						Move m = MoveProducer.createMultiMove(pl, j, j, (DoubleMoveCard) ext.getItem(pl, DoubleMoveCard.class), subMoves);
						m.setItem(new DoubleMoveCard());
						p.checkMove(g, gg, m);
						fail("move exception expected");
					} catch (IllegalMoveException e) {
						if (!e.getMessage().contains("stolen"))
							fail("not the expected exception: " + e.getMessage());
					}
					
					
				}
				
				
				
				
				
				// sauberen move adden, damit der test in die naechste runde gehen kann
				g.getMoves().add(MoveProducer.createSingleMove(pl, j, j, s4, tc1, (Ticket) ext.getItem(pl, TaxiTicket.class)));
			}
		}
	}
	
	@Test
	public final void testGetNextItemOwner() {
		// mrX
		// pos
		Move m1 = MoveProducer.createInitialMove(mrX, s2);
		Move m2 = MoveProducer.createSingleMove(mrX, 1, 1, s1, bc1, blt);		
		subMoves = new SubMoves()
				.add(s3, uc1, ut)
				.add(s4, tc1, tt);
		Move m3 = MoveProducer.createMultiMove(mrX, 2, 2, dmc, subMoves);  
		
		assertEquals(null, p.getNextItemOwner(g, m1, m1.getItem()));
		assertEquals(null, p.getNextItemOwner(g, m2, m2.getItem()));
		assertEquals(null, p.getNextItemOwner(g, m3, m3.getItem()));
		assertEquals(null, p.getNextItemOwner(g, m3, m3.getMoves().get(0).getItem()));
		assertEquals(null, p.getNextItemOwner(g, m3, m3.getMoves().get(1).getItem()));
		
		// neg
		try {
			assertEquals(null, p.getNextItemOwner(g, m1, tt));
			fail("exception expected");
		} catch (IllegalMoveException e) { }
		try {
			assertEquals(null, p.getNextItemOwner(g, m2, tt));
			fail("exception expected");
		} catch (IllegalMoveException e) { }
		try {
			assertEquals(null, p.getNextItemOwner(g, m3, blt));
			fail("exception expected");
		} catch (IllegalMoveException e) { }
		try {
			assertEquals(null, p.getNextItemOwner(g, m3, blt));
			fail("exception expected");
		} catch (IllegalMoveException e) { }
		try {
			assertEquals(null, p.getNextItemOwner(g, m3, blt));
			fail("exception expected");
		} catch (IllegalMoveException e) { }
	
		
		// detectives
		m1 = MoveProducer.createInitialMove(d1, s2);
		m2 = MoveProducer.createSingleMove(d1, 1, 1, s1, bc1, bt);
		
		assertEquals(null, p.getNextItemOwner(g, m1, m1.getItem()));
		assertEquals(g.getMrX(), p.getNextItemOwner(g, m2, m2.getItem()));
		
	}
	
	@Test
	public void testCanMove() {
		
		g.getMoves().clear();
		DetectivePlayer d3 = new DetectivePlayer();
		DetectivePlayer d4 = new DetectivePlayer();
		g.getDetectives().add(d3);
		g.getDetectives().add(d4);
		
		for (Player p : g.getPlayers()) {
			g.setItems(p, r.getGameInitPolicy().createItemSet(g, p));
		}
		
		// Phase 1: Test if exception come on illegal state
		for (Player p : g.getPlayers()) {
			try {
				this.p.canMove(g, gg, p);
				fail("IllegalStateException fail to appear");
			} catch (IllegalStateException e) { }
			g.getMoves().add(MoveProducer.createInitialMove(p,
					r.getGameInitPolicy().suggestInitialStation(g, gg, p)));			
		}
		// so, jetzt haben alle player initial moves
		
		// Phase 2: Wenn jeder einen Initial Move hat,
		// 		    sollte canMove fuer jeden true sein
		for (Player p : g.getPlayers()) {
			assertTrue(this.p.canMove(g, gg, p));
		}
		
		// Phase 3: Ohne gueltige Tickets
		ItemDealer dealer = new ItemDealer(g);
		for (Player p : g.getPlayers()) {
			dealer.removeAllItems(p, TaxiTicket.class);
			dealer.removeAllItems(p, BusTicket.class);
			dealer.removeAllItems(p, UndergroundTicket.class);
			
			if (p instanceof MrXPlayer)
				dealer.removeAllItems(p, BlackTicket.class);
			
			assertTrue(!this.p.canMove(g, gg, p));
		}
		
		// Phase 4: Player ist wirklich umzingelt
		// (siehe original Spielplan)
		
		for (Player p : g.getPlayers()) { // die wurden vorher ja geloescht...
			g.setItems(p, r.getGameInitPolicy().createItemSet(g, p));
		}
		
		// MrX umzingelt
		g.getMoves().add(MoveProducer.createInitialMove(
				mrX, nsm.get(166)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d1, nsm.get(153)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d2, nsm.get(151)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d3, nsm.get(181)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d4, nsm.get(183)));		
		assertTrue(!p.canMove(g, gg, mrX));
		for (Player pl : g.getDetectives()) {
			assertTrue(p.canMove(g, gg, pl));
		}
		
		// MrX umzingelt, aber mit Fernverkehranschluss
		g.getMoves().add(MoveProducer.createInitialMove(
				mrX, nsm.get(34)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d1, nsm.get(47)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d2, nsm.get(22)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d3, nsm.get(48)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d4, nsm.get(10)));		
		assertTrue(p.canMove(g, gg, mrX));
		for (Player pl : g.getDetectives()) {
			assertTrue(p.canMove(g, gg, pl));
		}
		
		// d1 umzingelt, aber u.a. von mrX
		g.getMoves().add(MoveProducer.createInitialMove(
				mrX, nsm.get(153)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d1, nsm.get(166)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d2, nsm.get(151)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d3, nsm.get(181)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d4, nsm.get(183)));

		for (Player pl : g.getPlayers()) {
			assertTrue(p.canMove(g, gg, pl));
		}
		
		// d1 umzingelt von anderen detectives
		g.getMoves().add(MoveProducer.createInitialMove(
				d1, nsm.get(96)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d2, nsm.get(77)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d3, nsm.get(109)));
		g.getMoves().add(MoveProducer.createInitialMove(
				d4, nsm.get(97)));
		
		for (Player pl : g.getPlayers()) {
			if (pl == d1)
				assertTrue(!p.canMove(g, gg, pl));
			else
				assertTrue(p.canMove(g, gg, pl));
		}
		
	}
	

}
