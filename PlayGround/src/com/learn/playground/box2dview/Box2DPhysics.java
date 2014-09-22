package com.learn.playground.box2dview;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Box2DPhysics {

	private static final int MAXIMUM_NUMBER_OF_GENERATED_BALLS = 10;

	private static final float GRAVITY = -12.5f;

	private static final int NO_OF_STEPS = 40;

	private static final float BALL_RADIUS = 1.2f;

	public static final float TUNNEL_RADIUS = 14.0f;

	private static final int VELOCITY_ITERATIONS = 8; // how strongly to correct
														// velocity
	private static final int POSITION_ITERATIONS = 3; // how strongly to correct
														// position

	private Body[] balls;
	private ArrayList<Body> generatedBalls;

	private World world;

	private float tunnelRadius;
	private float ballRadius;

	private int noOfBalls;

	public Box2DPhysics(float dimensionView, int noOfBalls) {
		this.noOfBalls = noOfBalls;

		Vec2 gravity = new Vec2(0f, GRAVITY);
		boolean doSleep = true;
		world = new World(gravity, doSleep);

		initValues(dimensionView);

		generatedBalls = new ArrayList<Body>();

		setupWorld();

	}

	public void initValues(float dimensionView) {

		tunnelRadius = dimensionView / 2f;
		ballRadius = Box2DPhysics.BALL_RADIUS * tunnelRadius
				/ Box2DPhysics.TUNNEL_RADIUS;

	}

	public float getTunnelRadius() {
		return tunnelRadius;
	}

	public float getBallRadius() {
		return ballRadius;
	}

	public void setupWorld() {

		setupBallTunnel();

		balls = new Body[noOfBalls];
		for (int i = 0; i < noOfBalls; i++) {
			balls[i] = createBall(new Vec2(
					(float) (Math.random() * TUNNEL_RADIUS) - TUNNEL_RADIUS
							/ 2f, (float) (Math.random() * TUNNEL_RADIUS / 2f)));
		}
	}

	public void resetWorld() {
		for (int i = 0; i < balls.length; i++) {
			world.destroyBody(balls[i]);
		}

		for (int i = 0; i < generatedBalls.size(); i++) {
			world.destroyBody(generatedBalls.get(i));
		}

		generatedBalls.clear();
		balls = new Body[noOfBalls];
		for (int i = 0; i < noOfBalls; i++) {
			balls[i] = createBall(new Vec2(
					(float) (Math.random() * TUNNEL_RADIUS) - TUNNEL_RADIUS
							/ 2f, (float) (Math.random() * TUNNEL_RADIUS / 2f)));
		}
	}

	public void setupBallTunnel() {

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		// bd.position = new Vec2(0.0f, SPINNER_RADIUS);

		Body tunnelBody = world.createBody(bd);
		PolygonShape shape = new PolygonShape();

		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;

		float angleDiff = 360.0f / NO_OF_STEPS;

		float radius = TUNNEL_RADIUS;
		float posX = 0.0f;
		float posY = 0.0f;

		for (int i = 0; i < NO_OF_STEPS; i++) {

			float initialAngle = i * angleDiff;
			float finalAngle = (i + 1) * angleDiff;
			shape.setAsEdge(
					new Vec2((float) (posX + radius
							* Math.cos(initialAngle * Math.PI / 180)),
							(float) (posY + radius
									* Math.sin(initialAngle * Math.PI / 180))),
					new Vec2((float) (posX + radius
							* Math.cos(finalAngle * Math.PI / 180)),
							(float) (posY + radius
									* Math.sin(finalAngle * Math.PI / 180))));

			fd.shape = shape;
			tunnelBody.createFixture(fd);
		}
	}

	private Body createBall(Vec2 position) {

		float radius = BALL_RADIUS;
		CircleShape shape = new CircleShape();
		shape.m_p.setZero();
		shape.m_radius = radius;

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 10.0f;
		fd.friction = 1.0f;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = position;
		Body body = world.createBody(bd);
		body.createFixture(fd);

		return body;
	}

	public void update(float dt) {
		world.step(dt, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}

	public void generateBall() {
		if (generatedBalls.size() < MAXIMUM_NUMBER_OF_GENERATED_BALLS)
			generatedBalls
					.add(createBall(new Vec2(
							(float) (Math.random() * TUNNEL_RADIUS)
									- TUNNEL_RADIUS / 2f, (float) (Math
									.random() * TUNNEL_RADIUS / 2f))));
	}

	public float getScaledValue(float x) {
		return x * getTunnelRadius() / TUNNEL_RADIUS;
	}

	public World getWorld() {
		return world;
	}

}
