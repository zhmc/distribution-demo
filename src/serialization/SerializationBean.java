package serialization;

import java.io.Serializable;

/**
 * 一个用户 POJO
 *
 */
public class SerializationBean implements Serializable {


	private static final long serialVersionUID = 3235432002462705915L;
	private long age;
	private String name;

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "name:" + this.getName() + ";age:" + this.getAge();

	}

	public SerializationBean() {
	}

}
