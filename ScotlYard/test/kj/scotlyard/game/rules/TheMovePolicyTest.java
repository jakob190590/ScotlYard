package kj.scotlyard.game.rules;

import static org.junit.Assert.*;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TheGame;
import kj.scotlyard.game.model.TheMoveProducer;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;

import org.junit.Before;
import org.junit.Test;

public class TheMovePolicyTest {

	Rules r;
	MovePolicy p;
	
	TheMoveProducer producer = TheMoveProducer.createInstance();
	
	Game g;
	GameGraph gg;
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
		
		tt = new TaxiTicket();
		ut = new UndergroundTicket();
		bt = new BusTicket();
		blt = new BlackTicket();
		
		dmc = new DoubleMoveCard();
		
		mrX = new MrXPlayer();
		d1 = new DetectivePlayer();
		d2 = new DetectivePlayer();
		
		g = new TheGame();
		ext = new GameStateExtension(g);
		g.setMrX(mrX);
		g.getDetectives().add(d1);
		g.getDetectives().add(d2);
		
		for (Player p : g.getPlayers()) {
			g.setItems(p, r.getGameInitPolicy().createItemSet(g, p));
		}
		
		s1 = new Station();
		s2 = new Station();
		s3 = new Station();
		s4 = new Station();
		
		tc1 = new TaxiConnection();
		uc1 = new UndergroundConnection();
		bc1 = new BusConnection();
		fc1 = new FerryConnection();
		tc2 = new TaxiConnection();
		uc2 = new UndergroundConnection();
		bc2 = new BusConnection();
		fc2 = new FerryConnection();

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
		
		ms[0][0] = producer.createInitialMove(mrX, s2);
		ms[1][0] = producer.createSingleMove(mrX, 1, 1, s1, bc1, (Ticket) getItem(mrX, BusTicket.class));
		producer.addSubMove(s3, uc1, (Ticket) getItem(mrX, UndergroundTicket.class)); 
		producer.addSubMove(s4, tc1, (Ticket) getItem(mrX, TaxiTicket.class));  
		ms[2][0] = producer.createMultiMove(mrX, 2, 2, (DoubleMoveCard) getItem(mrX, DoubleMoveCard.class));
		ms[3][0] = producer.createSingleMove(mrX, 3, 4, s1, uc1, (Ticket) getItem(mrX, UndergroundTicket.class));
		
		
		ms[0][1] = producer.createInitialMove(d1, s1);
		ms[1][1] = producer.createSingleMove(d1, 1, 1, s2, uc1, (Ticket) getItem(d1, UndergroundTicket.class));
		ms[2][1] = producer.createSingleMove(d1, 2, 2, s3, bc1, (Ticket) getItem(d1, BusTicket.class));  
		ms[3][1] = producer.createSingleMove(d1, 3, 3, s4, tc1, (Ticket) getItem(d1, TaxiTicket.class));
		
		
		ms[0][2] = producer.createInitialMove(d2, s1);
		ms[1][2] = producer.createSingleMove(d2, 1, 1, s2, tc1, (Ticket) getItem(d2, TaxiTicket.class));
		ms[2][2] = producer.createSingleMove(d2, 2, 2, s3, uc1, (Ticket) getItem(d2, UndergroundTicket.class));  
		ms[3][2] = producer.createSingleMove(d2, 3, 3, s4, bc1, (Ticket) getItem(d2, BusTicket.class));
		
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
					// missing station
					p.checkMove(g, gg, producer.createInitialMove(pl, j, null));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("station"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				try {
					// not players turn
					p.checkMove(g, gg, producer.createInitialMove(new MrXPlayer(), s1));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("turn"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong round number
					p.checkMove(g, gg, producer.createInitialMove(pl, 2, s2));
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("round number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass item in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.setItem(dmc);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("item"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass ticket in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.setItem(tt);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("item"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass connection in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.setConnection(bc1);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("connection"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass move index in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.setMoveIndex(0);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move index"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong number in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.setMoveNumber(1);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// pass ticket in initial move
					Move m = producer.createInitialMove(pl, j, s3);
					m.getMoves().add(producer.createSingleMove(pl, 0, 0, s3, bc1, tt));
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
	public final void testSingleMove() {
		g.setCurrentRoundNumber(0);
		
		for (Player pl : g.getPlayers()) {
			g.setCurrentPlayer(pl);
			Move m = producer.createInitialMove(pl, s1);
			p.checkMove(g, gg, m);
			g.getMoves().add(m);
		}
		
		for (int j = 1; j < 3; j++) {
			g.setCurrentRoundNumber(j);
			
			for (Player pl : g.getPlayers()) {
				g.setCurrentPlayer(pl);
				
				
				try {
					// invalid round number
					Move m = producer.createSingleMove(pl, j+3, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("round number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// invalid move number
					Move m = producer.createSingleMove(pl, j, j+3, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move number"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong player
					Move m = producer.createSingleMove(new DetectivePlayer(), j, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("turn"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// wrong move index
					Move m = producer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					m.setMoveIndex(0);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("move index"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// station null
					Move m = producer.createSingleMove(pl, j, j, null, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("station"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// stolen ticket
					Move m = producer.createSingleMove(pl, j, j, s4, bc1, bt);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("stolen"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// invalid ticket
					Move m = producer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, TaxiTicket.class));
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("not valid"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				try {
					// not ticket but double move card
					Move m = producer.createSingleMove(pl, j, j, s4, bc1, (Ticket) ext.getItem(pl, BusTicket.class));
					m.setItem(dmc);
					p.checkMove(g, gg, m);
					fail("move exception expected");
				} catch (IllegalMoveException e) {
					if (!e.getMessage().contains("provide a ticket"))
						fail("not the expected exception: " + e.getMessage());
				}
				
				
				// sauberen move adden, damit der test in die naechste runde gehen kann
				g.getMoves().add(producer.createSingleMove(pl, j, j, s4, tc1, (Ticket) ext.getItem(pl, TaxiTicket.class)));
			}
		}
		
		
		
	}
	
	@Test
	public final void testMultiMove() {
		g.setCurrentRoundNumber(0);
		
		for (Player pl : g.getPlayers()) {
			g.setCurrentPlayer(pl);
			Move m = producer.createInitialMove(pl, s1);
			p.checkMove(g, gg, m);
			g.getMoves().add(m);
		}
		
		
		
		
//		try {
//			Move m = producer.;
//			producer.addSubMov
//			producer.addSubMov
//			p.checkMove(g, gg, m);
//			fail("move exception expected");
//		} catch (IllegalMoveException e) {
//			if (!e.getMessage().contains(""))
//				fail("not the expected exception: " + e.getMessage());
//		}
	}
	
	@Test
	public final void testGetNextItemOwner() {
		// mrX
		// pos
		Move m1 = producer.createInitialMove(mrX, s2);
		Move m2 = producer.createSingleMove(mrX, 1, 1, s1, bc1, blt);		
		producer.addSubMove(s3, uc1, ut);
		producer.addSubMove(s4, tc1, tt);
		Move m3 = producer.createMultiMove(mrX, 2, 2, dmc);  
		
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
		m1 = producer.createInitialMove(d1, s2);
		m2 = producer.createSingleMove(d1, 1, 1, s1, bc1, bt);
		
		assertEquals(null, p.getNextItemOwner(g, m1, m1.getItem()));
		assertEquals(g.getMrX(), p.getNextItemOwner(g, m2, m2.getItem()));
		
	}
	

}
