package edu.cs6491Final;

/**
 * Created by bobby on 11/22/14.
 */
public class GeneratedPoint extends Point{

	public double r;

	public GeneratedPoint(double r, Point p) {
		super(p.x, p.y, p.z);
		this.r = r;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		GeneratedPoint that = (GeneratedPoint) o;

		if (Double.compare(that.r, r) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(r);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	public boolean pointEquals(GeneratedPoint q) {
		return super.equals(q);
	}
}