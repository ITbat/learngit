public class Complex {
	private double real;
	private double img;
	
	/**
	 * 三个构造函数
	 */
	public Complex() {
		this.real = 0;
		this.img = 0;
	}
	
	public Complex(double real, double img) {
		this.real = real;
		this.img = img;
	}
	
	public Complex(Complex c) {
		this.real = c.getReal();
		this.img = c.getImg();
	}
	
	/** 
	 * 获得实数部分
	 */
	public double getReal() {
		return real;
	}

	/** 
	 * 获得虚数部分
	 */
	public double getImg() {
		return img;
	}
	
	/** 
	 * 两个赋值函数
	 */
	public void setValue(Complex c) {
		this.real = c.getReal();
		this.img = c.getImg();
	}
	
	public void setValue(double r, double i) {
		this.real = r;
		this.img = i;
	}
	
	/**
	 * 求一个复数的共轭
	 */
	public Complex conjugate() {
		Complex res = new Complex();
		res.setValue(this.getReal(), this.getImg() * (-1));
		return res;
	}

	/**
	 * 求一个复数和一个实型数据的和；
	 */
	public Complex add(double x) {
		Complex res = new Complex(this);
		res.real += x;
		return res;
	}
	
	/**
	 * 用于求解两复数的和
	 */
	public Complex add(Complex c) {
		Complex res = new Complex(this);
		res.real += c.getReal();
		res.img += c.getImg();
		return res;
	}
	
	/**
	 * 用于求一个复数和一个实型数据的差；
	 */
	public Complex minus(double x) {
		Complex res = new Complex(this);
		res.real -= x;
		return res;
	}
	
	/**
	 * 用于求解两复数的差
	 */
	public Complex minus(Complex c) {
		Complex res = new Complex(this);
		res.real -= c.getReal();
		res.img -= c.getImg();
		return res;
	}
	
	/**
	 * 求一个复数和一个实型数据的积
	 */
	public Complex multiply(double x) {
		Complex res = new Complex(this);
		res.real *= x;
		res.img *= x;
		return res;
	}
		
	/**
	 * 求两个复数的积
	 */
	public Complex multiply(Complex c) {
		Complex res = new Complex();
		double r1 = this.real, i1 = this.img;
		double r2 = c.getReal(), i2 = c.getImg();
		res.real = r1 * r2 - i1 * i2;
		res.img = r1 * i2 + r2 * i1;
		return res;
	}
		
	/**
	 * 求一个复数和一个实型数据的商
	 */
	public Complex division(double x) {
		Complex res = new Complex(this);
		res.real /= x;
		res.img /= x;
		return res;
	}
		
	/**
	 * 求两个复数的商
	 */
	public Complex division(Complex c) {
		Complex res = new Complex();
		double r1 = this.real, i1 = this.img;
		double r2 = c.getReal(), i2 = c.getImg();
		res.real = (r1*r2 + i1*i2) / (r2*r2 + i2*i2);
		res.img = (r2*i1 - r1*i2) / (r2*r2 + i2*i2);
		return res;
	}
	
	/**
	 * 求复数的模
	 */
	public double model() {
		return Math.sqrt(real * real + img * img);
	}
		
	@Override
	public String toString() {
		if (img >= 0) {
			return real + "+" + img + "i";
		} else {
			return real + "" + img + "i";
		}
	}

}
