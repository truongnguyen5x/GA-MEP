package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

public class Main {
	public static void main(String[] args)  {
		File f = new File("data");
		for (int idf = 0; idf < f.listFiles().length; ++idf) {
			String input = "data/" + f.list()[idf];
			System.out.println("Processing: " + input);
			
			Topo topo = new Topo();
			topo.construct(input);
			
			double averageTime = 0;
			double averageBest = 0;
			double best = Double.POSITIVE_INFINITY;
			int bestRun = 0;
			for (int ir = 0; ir < Config.NUM_RUN; ++ir) {
				System.out.println("lan chay: " + ir);
				Population p = new Population();
				String convergence = "Convergence\n";
				double t1 = System.currentTimeMillis();
				p.initialize();
				p.calFitness(topo);
				p.sort();
				
				Individual bestIndividual = p.getIndividualByIndex(0);
				convergence += bestIndividual.getFitness() + "\n";
				int noBetter = 0;
				for (int i = 0; i < Config.GENERATION_NUM; ++i) {
					
						Population offs = p.evolve(topo);
						offs.sort();
						p = p.mergeSort(offs);
						if (p.getIndividualByIndex(0).getFitness() < bestIndividual.getFitness()) {
							bestIndividual = p.getIndividualByIndex(0);
							noBetter = 0;
						} else {
							++noBetter;
						}
						if (Double.compare(bestIndividual.getFitness(), 0.0) == 0 ) {
							System.out.println("Found Optimal solution");
							break;
						} 
						convergence += bestIndividual.getFitness() + "\n";
						System.out.println(bestIndividual.getFitness());
					
				}
				double t2 = System.currentTimeMillis();
				averageTime += (t2 - t1);
				double bestValue = calculate(convergence, bestIndividual, topo, input, ir);
				if (bestValue < best){
					best = bestValue;
					bestRun = ir;
				}
				averageBest += bestValue;
			}
			String sumary ="Average time: " +averageTime / (Config.NUM_RUN * 1000) + "\n";
			sumary += "Average mep: " + averageBest / Config.NUM_RUN + "\n";
			sumary += "Best value: " + best + "\n";
			sumary += "Best run: " + bestRun + "\n";
			sumary += calDijkstra(topo, input) + "\n";
			writeSummary(sumary, input);
		}

	}
	

	public static void writeSummary(String sum, String filename){
		filename = filename.replaceFirst("data", "result");
		filename = filename.replaceFirst("\\.", "\\_summary.");
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write(sum);
			
			bw.flush();
		} catch(Exception ex) {
			System.out.println("Write error 2");
		}finally{
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void writeResult(Population p, String filename){
		filename = filename.replaceFirst("data", "result");
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < p.getSize(); ++i) {
				bw.write("Individual ");
				bw.flush();
				Iterator<Point> iter = p.getIndividualByIndex(i).getListGene().iterator();
				String str = "";
				
				while(iter.hasNext()){
					Point po = iter.next();
					if (po.getX() != po.getX()){
						System.out.println("indi" + i + "writefile x");
					}
					if (po.getY() != po.getY()){
						System.out.println("indi" + i + "writefile Y");
					}
					str += "(" + df.format(po.getX()) + "," + df.format(po.getY()) + ")";
				}
				str += "\n";
				bw.write(str);
				bw.flush();
			}
		} catch(Exception ex) {
			System.out.println("Write error");
		}finally{
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static void writeResult(String convergence, Individual indi, String filename, int ir){
		filename = filename.replaceFirst("data", "result");
		filename = filename.replaceFirst("\\.", "_"+ir+"\\.");
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write(convergence);
			bw.flush();
			
			Iterator<Point> iter = indi.getListGene().iterator();
			String str = "NumberOfPoint \n";
			str += indi.getLength() + "\n";
			while(iter.hasNext()){
				Point po = iter.next();
				str += "" + df.format(po.getX()) + " " + df.format(po.getY()) + "\n";
			}
			str += "Value\n";
			str += indi.getFitness();
			bw.write(str);
			bw.flush();
		} catch(Exception ex) {
			System.out.println("Write error 3");
		}finally{
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static double calculate(String convergence, Individual best, Topo topo, String filename, int ir) {
		int nb = best.getLength();
		int nbChia = (int)(Config.len);
		Individual in2 = new Individual();
		int nbPoint = 0;
		Point endp = new Point(Config.W, Config.endY);
		for (int i = 0; i < nb - 2; ++i) {
			in2.addGene(best.getGene(i));
			++nbPoint;
			double phi = Math.atan((best.getGene(i + 1).getY() - best.getGene(i).getY())
					/ (best.getGene(i + 1).getX() - best.getGene(i).getX()));
			for (int j = 1; j < nbChia; ++j) {
				double x1, y1;
				if (Double.compare(best.getGene(i + 1).getX(), best.getGene(i).getX()) == 0) {
					x1 = best.getGene(i).getX();
				} else {
					x1 = in2.getGene(nbPoint - 1).getX() + Config.len2 * Math.cos(phi);
				}
					y1 = in2.getGene(nbPoint - 1).getY() + Config.len2 * Math.sin(phi);
				in2.addGene(new Point(x1, y1));
				++nbPoint;
			}
		}
		in2.addGene(best.getGene(nb - 2)); ++nbPoint;
		double deltaX, deltaY;
		int nbp = (int) Math.floor(in2.getLastGene().distance(endp) / Config.len2);
		if (Double.compare(endp.getX(), in2.getLastGene().getX()) == 0) {
			deltaY = Config.len * ((endp.getY() - in2.getLastGene().getY()) < 0 ? -1 : 1);
			for (int ip = 0; ip < nbp; ++ip) {
				in2.addGene(new Point(endp.getX(), in2.getLastGene().getY() + deltaY));
			}
		} else {
			double phi = Math.atan((endp.getY() - in2.getLastGene().getY()) / (endp.getX() - in2.getLastGene().getX()));
			deltaX = Config.len * Math.cos(phi);
			deltaY = Config.len * Math.sin(phi);
			for (int ip = 0; ip < nbp; ++ip) {
				in2.addGene(new Point(in2.getLastGene().getX() + deltaX, in2.getLastGene().getY() + deltaY));
			}
		}
		if (Double.compare(in2.getLastGene().distance(endp), 0.0) != 0) {
			in2.addGene(endp);
		}
		/*if (Double.compare(best.getGene(nb - 1).getX(), best.getGene(nb - 2).getX()) == 0) {
			boolean isBelow = best.getGene(nb - 1).getY() < endp.getY() ? true : false;
			if (isBelow) {
				while(in2.getGene(in2.getLength() - 1).getY() +  Config.len2 < endp.getY()) {
					in2.addGene(new Point(endp.getX(), in2.getGene(in2.getLength()-1).getY() +  Config.len2));
				}
				
			} else { 
				while(in2.getGene(in2.getLength() - 1).getY() -  Config.len2 > endp.getY()) {
					in2.addGene(new Point(endp.getX(), in2.getGene(in2.getLength()-1).getY() -  Config.len2));
				}
			}
		} else {
			double phi = Math.atan((best.getGene(nb-1).getY() - best.getGene(nb - 2).getY())
					/ (best.getGene(nb - 1).getX() - best.getGene(nb - 2).getX()));
			
			for (int i = 0; i < nbp; ++i) {
				
			}
			while (in2.getGene(nbPoint-1).distance(endp) > Config.len2) {
				double x1 = in2.getGene(nbPoint - 1).getX() + Config.len2 * Math.cos(phi);
				double y1 = in2.getGene(nbPoint - 1).getY() + Config.len2 * Math.sin(phi);
				in2.addGene(new Point(x1, y1));
				++nbPoint;
				if (x1 > Config.W || y1 > Config.H) {
					System.out.println("x: " + x1 + " - " + y1);
					in2.printPath2();
					break;
				}
			}
		}
		if (in2.getGene(in2.getLength()-1).distance(endp) != 0) {
			in2.addGene(endp);
			
		}*/
		in2.calFitness(topo);
		writeResult(convergence, in2, filename, ir);
		return in2.getFitness();
	}
	public static double calDijkstra(Topo topo, String filename) {
		filename = filename.replaceFirst("data/", "resultDi/");
	
		File f = new File(filename);
		FileReader fr = null;
		BufferedReader br = null;
		Individual indi = new Individual();
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String str = br.readLine();
			str = br.readLine();
			int nb = Integer.valueOf(str);
			str = br.readLine(); 
			for (int i = 0; i < nb; ++i) {
				str = br.readLine();
				double x = Double.valueOf(str.split(" ")[0]);
				double y = Double.valueOf(str.split(" ")[1]);
				indi.addGene(new Point(x, y));
			}
			indi.calFitness(topo);
			System.out.println("Dij fitness:" + indi.getFitness());
			
			
		} catch (Exception ex) {
			System.out.println("Dijkstra read error");
		} finally{
			if (null != fr) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return indi.getFitness();
	}
}
