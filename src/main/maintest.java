package main;

public class maintest {
	public static void main(String[] args) {
		test1();
	}

	public static void test2() {
		Topo topo = new Topo();
		topo.construct("data/d_20_2.txt");
	}

	public static void test1() {
		Topo topo = new Topo();
		topo.construct("data/d_20_2.txt");
		Population p = new Population();
		double t1 = System.currentTimeMillis();
		p.initialize();
		p.calFitness(topo);
		p.sort();
		Individual bestIndividual = p.getIndividualByIndex(0);
		int noBetter = 0;
		for (int i = 0; i < Config.GENERATION_NUM; ++i) {
			System.out.println("\n lan"+i );
			Population offs = p.evolve(topo);
			offs.sort();
			p = p.mergeSort(offs);
			if (p.getIndividualByIndex(0).getFitness() < bestIndividual.getFitness()) {
				bestIndividual = p.getIndividualByIndex(0);
				noBetter = 0;
			} else {
				++noBetter;
			}
			if (Double.compare(bestIndividual.getFitness(), 0.0) == 0) {
				System.out.println("Found Optimal solution");
				break;
			}
		}
		double t2 = System.currentTimeMillis();
		double Time = (t2 - t1);
		System.out.println("\n time " + (Time / 1000) + " seconds");
		System.out.println("better " + noBetter);
		System.out.println("best " + bestIndividual.calFitness(topo));
		bestIndividual.printPath2();
	}

}
