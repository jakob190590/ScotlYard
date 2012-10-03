/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.game.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

import org.junit.Before;
import org.junit.Test;

public class TheGameStateAccessPolicyTest {

	private GameStateAccessPolicy policy;

	private Game g;
	GameGraph gg = null;

	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[100];
	Move m1, m2;

	SubMoves subMoves;
	
	@Before
	public void setUp() throws Exception {
		TheRules rules = new TheRules();
		policy = rules.getGameStateAccessPolicy();

		g = new DefaultGame();

		mrX = new MrXPlayer();
		d1 = new DetectivePlayer();
		d2 = new DetectivePlayer();
		d3 = new DetectivePlayer();
		d4 = new DetectivePlayer();

		g.setMrX(mrX);
		g.getDetectives().add(d1);
		g.getDetectives().add(d2);
		g.getDetectives().add(d3);
		g.getDetectives().add(d4);

		int j = 0;
		for (int i = 0; i < 20; i++) {
			for (Player p : g.getPlayers()) {
				ms[j] = MoveProducer.createSingleMove(p, i, i,
						new Station(gg), new TaxiConnection(gg), new TaxiTicket());
				j++;
			}
		}

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		m2 = ms[5] = MoveProducer.createMultiMove(mrX, 1, 1, new DoubleMoveCard(), subMoves);
		m1 = m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		m2 = ms[15] = MoveProducer.createMultiMove(mrX, 3, 4, new DoubleMoveCard(), subMoves);
		m1 = m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);

		int n = 0;
		for (Move m : ms) {

			if (m.getPlayer() == mrX) {
				if (m.getMoveNumber() >= 0)
					m.setMoveNumber(n++);
				else
					n += m.getMoves().size();
			}

			g.getMoves().add(m);
		}

//		for (Move m : g.getMoves()) {
//			if (m.getPlayer() == mrX) {
//				System.out.println("round number: " + m.getRoundNumber()
//						+ "    move number: " + m.getMoveNumber());
//				for (Move o : m.getMoves()) {
//					System.out.println("round number: " + o.getRoundNumber()
//							+ "    move number: " + o.getMoveNumber());
//				}
//			}
//		}

	}

	@Test
	public final void testCreateGameStateForDetectives() {
		GameState gs = policy.createGameStateForDetectives(g);
		// test results of move access methods of a Game

		// equals von MaskedMove
		assertEquals(gs.getMoves().get(0),
				gs.getMove(mrX, 0, GameState.MoveAccessMode.ROUND_NUMBER));
		assertEquals(gs.getMoves().get(0),
				gs.getMove(mrX, 0, GameState.MoveAccessMode.MOVE_NUMBER));
		assertEquals(gs.getMove(mrX, 0, GameState.MoveAccessMode.ROUND_NUMBER),
				gs.getMove(mrX, 0, GameState.MoveAccessMode.MOVE_NUMBER));

		assertEquals(gs.getMoves().get(-5),
				gs.getMove(mrX, -1, GameState.MoveAccessMode.ROUND_NUMBER));
		assertEquals(gs.getMoves().get(-5),
				gs.getMove(mrX, -1, GameState.MoveAccessMode.MOVE_NUMBER));
		assertEquals(
				gs.getMove(mrX, -1, GameState.MoveAccessMode.ROUND_NUMBER),
				gs.getMove(mrX, -1, GameState.MoveAccessMode.MOVE_NUMBER));

		// getMoves()
		// pos test
		for (Move m : gs.getMoves()) {
			if (m.getPlayer() instanceof DetectivePlayer) {
				try {
					m.getConnection();
					m.getStation();

				} catch (IllegalAccessException e) {
					fail("there should be no illegal access");
				}

				// fuer sub moves
				for (Move n : m.getMoves()) {
					if (n.getPlayer() instanceof DetectivePlayer) {
						try {
							n.getConnection();
							n.getStation();
						} catch (IllegalAccessException e) {
							fail("there should be no illegal access");
						}
					} else if (policy.getMrXUncoverMoveNumbers().contains(
							n.getMoveNumber())) {
						try {
							n.getStation();
						} catch (IllegalAccessException e) {
							fail("there should be no illegal access");
						}
					}
				}
			} else if (policy.getMrXUncoverMoveNumbers().contains(
					m.getMoveNumber())) {

				try {
					m.getStation();

				} catch (IllegalAccessException e) {
					fail("there should be no illegal access");
				}
			}
		}

		// neg test
		for (Move m : gs.getMoves()) {
			if (m.getPlayer() instanceof MrXPlayer
					&& !policy.getMrXUncoverMoveNumbers().contains(
							m.getMoveNumber())) {
				try {
					m.getConnection();
					fail("there should be an illegal access");
				} catch (IllegalAccessException e) {
				}
				try {
					m.getStation();
					fail("there should be an illegal access");
				} catch (IllegalAccessException e) {
				}

				// fuer sub moves
				for (Move n : m.getMoves()) {
					if (n.getPlayer() instanceof MrXPlayer
							&& !policy.getMrXUncoverMoveNumbers().contains(
									n.getMoveNumber())) {
						try {
							n.getConnection();
							fail("there should be an illegal access");
						} catch (IllegalAccessException e) {
						}
						try {
							n.getStation();
							fail("there should be an illegal access");
						} catch (IllegalAccessException e) {
						}
					}
				}
			}
		}

		// getMove(...)
		// by round number and move number (only detectives)
		for (int i = 0; i < 20; i++) {
			for (Player p : gs.getPlayers()) {
				Move m = gs
						.getMove(p, i, GameState.MoveAccessMode.ROUND_NUMBER);

				// pos test
				if (m.getPlayer() instanceof DetectivePlayer
						|| policy.getMrXUncoverMoveNumbers().contains(
								m.getMoveNumber())) {
					try {
						m.getStation();

					} catch (IllegalAccessException e) {
						fail("there should be no illegal access");
					}

					// fuer sub moves
					for (Move n : m.getMoves()) {
						if (n.getPlayer() instanceof DetectivePlayer
								|| policy.getMrXUncoverMoveNumbers().contains(
										n.getMoveNumber())) {
							try {
								n.getStation();
							} catch (IllegalAccessException e) {
								fail("there should be no illegal access");
							}
						}
					}
				}

				// neg test
				if (m.getPlayer() instanceof MrXPlayer
						&& !policy.getMrXUncoverMoveNumbers().contains(
								m.getMoveNumber())) {
					try {
						m.getConnection();
						fail("there should be an illegal access");
					} catch (IllegalAccessException e) {
					}
					try {
						m.getStation();
						fail("there should be an illegal access");
					} catch (IllegalAccessException e) {
					}

					// fuer sub moves
					for (Move n : m.getMoves()) {
						if (n.getPlayer() instanceof MrXPlayer
								&& !policy.getMrXUncoverMoveNumbers().contains(
										n.getMoveNumber())) {
							try {
								n.getConnection();
								fail("there should be an illegal access");
							} catch (IllegalAccessException e) {
							}
							try {
								n.getStation();
								fail("there should be an illegal access");
							} catch (IllegalAccessException e) {
							}
						}
					}
				}
			}

			for (Player p : gs.getDetectives()) {
				Move m = gs.getMove(p, i, GameState.MoveAccessMode.MOVE_NUMBER);

				// pos test
				try {
					m.getConnection();
					m.getStation();

				} catch (IllegalAccessException e) {
					fail("there should be no illegal access");
				}
			}
		}

		// by move number
		for (int i = 0; i < 20; i++) {
			Move m = gs.getMove(gs.getMrX(), i,
					GameState.MoveAccessMode.MOVE_NUMBER);

			// pos test
			if (policy.getMrXUncoverMoveNumbers().contains(m.getMoveNumber())) {
				try {
					m.getStation();

				} catch (IllegalAccessException e) {
					fail("there should be no illegal access");
				}

				// fuer sub moves
				for (Move n : m.getMoves()) {
					if (policy.getMrXUncoverMoveNumbers().contains(
							n.getMoveNumber())) {
						try {
							n.getConnection();
							n.getStation();
						} catch (IllegalAccessException e) {
							fail("there should be no illegal access");
						}
					}
				}
			}

			// neg test
			if (!policy.getMrXUncoverMoveNumbers().contains(m.getMoveNumber())) {
				try {
					m.getConnection();
					fail("there should be an illegal access");
				} catch (IllegalAccessException e) {
				}
				try {
					m.getStation();
					fail("there should be an illegal access");
				} catch (IllegalAccessException e) {
				}

				// fuer sub moves
				for (Move n : m.getMoves()) {
					if (!policy.getMrXUncoverMoveNumbers().contains(
							n.getMoveNumber())) {
						try {
							n.getConnection();
							fail("there should be an illegal access");
						} catch (IllegalAccessException e) {
						}
						try {
							n.getStation();
							fail("there should be an illegal access");
						} catch (IllegalAccessException e) {
						}
					}
				}
			}
		}

		// getLastMove(...)
		while (!gs.getMoves().isEmpty()) {

			Move m = gs.getMoves().get(GameState.LAST_MOVE);

			if (m.getPlayer() instanceof DetectivePlayer) {
				// pos test
				try {
					m.getConnection();
					m.getStation();

				} catch (IllegalAccessException e) {
					fail("there should be no illegal access");
				}
			} else {

				// pos test
				if (policy.getMrXUncoverMoveNumbers().contains(
						m.getMoveNumber())) {
					try {
						m.getStation();

					} catch (IllegalAccessException e) {
						fail("there should be no illegal access");
					}

					// fuer sub moves
					for (Move n : m.getMoves()) {
						if (policy.getMrXUncoverMoveNumbers().contains(
								n.getMoveNumber())) {
							try {
								n.getConnection();
								n.getStation();
							} catch (IllegalAccessException e) {
								fail("there should be no illegal access");
							}
						}
					}
				}

				// neg test
				if (!policy.getMrXUncoverMoveNumbers().contains(
						m.getMoveNumber())) {
					try {
						m.getConnection();
						fail("there should be an illegal access");
					} catch (IllegalAccessException e) {
					}
					try {
						m.getStation();
						fail("there should be an illegal access");
					} catch (IllegalAccessException e) {
					}

					// fuer sub moves
					for (Move n : m.getMoves()) {
						if (!policy.getMrXUncoverMoveNumbers().contains(
								n.getMoveNumber())) {
							try {
								n.getConnection();
								fail("there should be an illegal access");
							} catch (IllegalAccessException e) {
							}
							try {
								n.getStation();
								fail("there should be an illegal access");
							} catch (IllegalAccessException e) {
							}
						}
					}
				}
			}

			g.getMoves().remove(GameState.LAST_MOVE);
		}

		// double moves testen
		g.getMoves().clear();

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		g.getMoves().add(
				m2 = MoveProducer.createMultiMove(mrX, 1, 1, new DoubleMoveCard(), subMoves));
		m1 = m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);
		
		m1 = gs.getMoves().get(0);
		try {
			m1.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m1.getStation();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}

		m2 = m1.getMoves().get(0);
		assertEquals(1, m2.getMoveNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}

		m2 = m1.getMoves().get(1);
		assertEquals(2, m2.getMoveNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		g.getMoves().add(m2 = MoveProducer.createMultiMove(mrX, 3, 3, new DoubleMoveCard(), subMoves));
		m1 = m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);

		m1 = gs.getMoves().get(1);
		try {
			m1.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m1.getStation();
			fail("there should be no illegal access");
		} catch (IllegalAccessException e) {
		}

		m2 = m1.getMoves().get(0);
		assertEquals(3, m2.getMoveNumber());
		assertEquals(3, m2.getRoundNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
		} catch (IllegalAccessException e) {
			fail("there should be an illegal access");
		}

		m2 = m1.getMoves().get(1);
		assertEquals(4, m2.getMoveNumber());
		assertEquals(3, m2.getRoundNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}

		
		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		g.getMoves().add(m2 = MoveProducer.createMultiMove(mrX, 3, 2, new DoubleMoveCard(), subMoves));
		m1 = m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);
		
		m1 = gs.getMoves().get(2);
		try {
			m1.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m1.getStation();
		} catch (IllegalAccessException e) {
			fail("there should be illegal access");
		}

		m2 = m1.getMoves().get(0);
		assertEquals(2, m2.getMoveNumber());
		assertEquals(3, m2.getRoundNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}

		m2 = m1.getMoves().get(1);
		assertEquals(3, m2.getMoveNumber());
		assertEquals(3, m2.getRoundNumber());
		try {
			m2.getConnection();
			fail("there should be an illegal access");
		} catch (IllegalAccessException e) {
		}
		try {
			m2.getStation();
		} catch (IllegalAccessException e) {
			fail("there should be illegal access");
		}
	}

	@Test
	public final void testGetMrXUncoverMoveNumbers() {
		try {
			policy.getMrXUncoverMoveNumbers().clear();
			fail("modifying ops work on unmodifiable List :S");
		} catch (Exception e) {
		}

		assertEquals(5, policy.getMrXUncoverMoveNumbers().size());
		assertEquals((Integer) 3, policy.getMrXUncoverMoveNumbers().get(0));
		assertEquals((Integer) 8, policy.getMrXUncoverMoveNumbers().get(1));
		assertEquals((Integer) 13, policy.getMrXUncoverMoveNumbers().get(2));
		assertEquals((Integer) 18, policy.getMrXUncoverMoveNumbers().get(3));
		assertEquals((Integer) 22, policy.getMrXUncoverMoveNumbers().get(4));
	}

}
