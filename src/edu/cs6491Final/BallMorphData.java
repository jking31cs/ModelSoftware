package edu.cs6491Final;

/**
 * Created by bobby on 11/22/14.
 */
public class BallMorphData {
	public Point p;
	public Point q;
	public Point center;

	public BallMorphData(Point p, Point q, Point center) {
		this.p = p;
		this.q = q;
		this.center = center;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public Point getQ() {
		return q;
	}

	public void setQ(Point q) {
		this.q = q;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BallMorphData that = (BallMorphData) o;

		if (center != null ? !center.equals(that.center) : that.center != null) return false;
		if (p != null ? !p.equals(that.p) : that.p != null) return false;
		if (q != null ? !q.equals(that.q) : that.q != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = p != null ? p.hashCode() : 0;
		result = 31 * result + (q != null ? q.hashCode() : 0);
		result = 31 * result + (center != null ? center.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "BallMorphData{" +
			"p=" + p +
			", q=" + q +
			", center=" + center +
			'}';
	}
}
