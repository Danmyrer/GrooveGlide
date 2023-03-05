package dev.henrihenr.game2d;

public class Vertex
{
	public double x;
	public double y;

	/**
	 * Vertex ohne Parameter
	 * 
	 * @implNote Konstruktor erstellt, da ich nicht immer
	 * {@code new Vertex(0, 0);} schreiben möchte
	 */
	public Vertex()
	{
		this.x = 0;
		this.y = 0;
	}

	public Vertex(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public void add(Vertex that)
	{
		x += that.x;
		y += that.y;
	}

	public void moveTo(Vertex that)
	{
		x = that.x;
		y = that.y;
	}

	public Vertex mult(double d)
	{
		return new Vertex(d * x, d * y);
	}
}
