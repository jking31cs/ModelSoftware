package edu.cs6491Final;

/**
 * Simple Matrix class, could be used further if needed.
 */
public class Matrix3D {

	public static final Matrix3D I = new Matrix3D(
		1,0,0,
		0,1,0,
		0,0,1
	);

	public final double
		a, b, c,
		d, e, f,
		g, h, i
	;


	public Matrix3D(
		double a, double b, double c,
		double d, double e, double f,
		double g, double h, double i) {

		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
	}

	public Vector mul(Vector v) {
		return new Vector(
			a*v.x + b*v.y + c*v.z,
			d*v.x + e*v.y + f*v.z,
			g*v.x + h*v.y + i*v.z
		);
	}
	
	public Matrix3D add(Matrix3D m) {
		return new Matrix3D(
			m.a + a, m.b + b, m.c + c,
			m.d + d, m.e + e, m.f + f,
			m.g + g, m.h + h, m.i + i
		);
	}

	public Matrix3D mul(double s) {
		return new Matrix3D(
			s*a,s*b,s*c,
			s*d,s*e,s*f,
			s*g,s*h,s*i
		);
	}

	/**
	 * Matrix that does cross products with another vector:
	 * 		u X v == Mv where M is a matrix formed from values of u.
	 *
	 * @param u
	 * @return
	 */
	public static Matrix3D crossMulMatrix(Vector u) {
		return new Matrix3D(
			0, -1*u.z, u.y,
			u.z, 0, -1*u.x,
			-1*u.y, u.x, 0
		);
	}
	public static Matrix3D tensorProductSelf(Vector u) {
		return new Matrix3D(
			u.x*u.x, u.x*u.y, u.x*u.z,
			u.y*u.x, u.y*u.y, u.y*u.z,
			u.z*u.x, u.z*u.y, u.z*u.z
		);
	}
}
