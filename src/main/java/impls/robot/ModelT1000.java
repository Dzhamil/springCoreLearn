package impls.robot;

import interfaces.Hand;
import interfaces.Head;
import interfaces.Leg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Getter
@Setter
public class ModelT1000 extends BaseModel implements  InitializingBean, DisposableBean {

	private String color;
	private int year;
	private boolean soundEnabled;

	public ModelT1000() {
		super();
	}

	public ModelT1000(Hand hand, Leg leg, Head head) {
		super(hand, leg, head);
	}

	public ModelT1000(Hand hand, Leg leg, Head head, String color, int year, boolean soundEnabled) {
		super(hand, leg, head);
		this.color = color;
		this.year = year;
		this.soundEnabled = soundEnabled;
	}

	public ModelT1000(String color, int year, boolean soundEnabled) {
		super();
		this.color = color;
		this.year = year;
		this.soundEnabled = soundEnabled;
	}

	@Override
	public void action() {
		Head head = getHead();
		Hand hand = getHand();
		Leg leg = getLeg();
		if (head != null && hand != null && leg != null) {
			getHead().calc();
			getHand().catchSomething();
			getLeg().go();
		} else {
			System.out.println("this is not base model");
		}
		System.out.println("color: " + color);
		System.out.println("year: " + year);
		System.out.println("can play sound: " + soundEnabled);
	}

	@Override
	public void dance() {
		System.out.println("T1000 is dancing!");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destroy");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("init");
	}

	public void initObject() {
		System.out.println("initObject");
	}

	public void destroyObject() {
		System.out.println("destroyObject ");
	}
}
