public class Complex {
	private double real;
	private double img;
	
	/**
	 * �������캯��
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
	 * ���ʵ������
	 */
	public double getReal() {
		return real;
	}

	/** 
	 * �����������
	 */
	public double getImg() {
		return img;
	}
	
	/** 
	 * ������ֵ����
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
	 * ��һ�������Ĺ���
	 */
	public Complex conjugate() {
		Complex res = new Complex();
		res.setValue(this.getReal(), this.getImg() * (-1));
		return res;
	}

	/**
	 * ��һ��������һ��ʵ�����ݵĺͣ�
	 */
	public Complex add(double x) {
		Complex res = new Complex(this);
		res.real += x;
		return res;
	}
	
	/**
	 * ��������������ĺ�
	 */
	public Complex add(Complex c) {
		Complex res = new Complex(this);
		res.real += c.getReal();
		res.img += c.getImg();
		return res;
	}
	
	/**
	 * ������һ��������һ��ʵ�����ݵĲ
	 */
	public Complex minus(double x) {
		Complex res = new Complex(this);
		res.real -= x;
		return res;
	}
	
	/**
	 * ��������������Ĳ�
	 */
	public Complex minus(Complex c) {
		Complex res = new Complex(this);
		res.real -= c.getReal();
		res.img -= c.getImg();
		return res;
	}
	
	/**
	 * ��һ��������һ��ʵ�����ݵĻ�
	 */
	public Complex multiply(double x) {
		Complex res = new Complex(this);
		res.real *= x;
		res.img *= x;
		return res;
	}
		
	/**
	 * �����������Ļ�
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
	 * ��һ��������һ��ʵ�����ݵ���
	 */
	public Complex division(double x) {
		Complex res = new Complex(this);
		res.real /= x;
		res.img /= x;
		return res;
	}
		
	/**
	 * ��������������
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
	 * ������ģ
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
