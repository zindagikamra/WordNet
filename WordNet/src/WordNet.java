import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.runners.AllTests;

import java.util.Iterator;

public class WordNet
{
	private Digraph graph;
	private Digraph reversedGraph;
	private ArrayList<Synset> synsetSet = new ArrayList<Synset>();
	private double rootAgg;
	private HashMap<String, Integer> nounsAll = new HashMap<String, Integer>();
	private int size;
	
	//private boolean[] marked = new boolean[82075];
	
	private class Synset
	{
		int id;
		HashSet<String> nouns = new HashSet<String>();
		int synsetCount;
		int aggFrequency;
	}
	
    public WordNet(String synsets, String hypernyms, String synsetCounts)
    {
    	// TODO:  You may use the code below to open and parse the
    	// synsets, hypernyms, and SynsetCounts files.  However, you MUST add your
    	// own code to actually store the file contents into the
    	// data structures you create as fields of the WordNet class.
    	
        // *** Parse synsets ***
        int largestId = -1;				// TODO: You might find this value useful 
        
        
        
        In inSynsets = new In(synsets);
        
        //graph = new Digraph(82075);

        while (inSynsets.hasNextLine())
        {
            String line = inSynsets.readLine();
            String[] tokens = line.split(",");
            
            // Synset ID
            int id = Integer.parseInt(tokens[0]);
            if (id > largestId)
            {
                largestId = id;
            }
            
    		//graph = new Digraph(largestId+1);
            size = largestId+1;
    		//System.out.println((System.currentTimeMillis()-start)+ " Time taken by code");

            // Nouns in synset
            String synset = tokens[1];
            String[] nouns = synset.split(" ");
            
            Synset s = new Synset();
            s.id = id;
            for (String noun : nouns)
            {
               // TODO: you should probably do something here
            	s.nouns.add(noun);	
            	nounsAll.put(noun, 1);
            }
            synsetSet.add(s);
            // tokens[2] is gloss, but we're not using that 
        }
        inSynsets.close();
        
        graph = new Digraph(size);
        

        
        // *** Parse hypernyms ***
        In inHypernyms = new In(hypernyms);
        while (inHypernyms.hasNextLine())
        {
            String line = inHypernyms.readLine();
            String[] tokens = line.split(",");
            
            int v = Integer.parseInt(tokens[0]);
            
            for (int i=1; i < tokens.length; i++)
            {
               // TODO: you should probably do something here
            	graph.addEdge(v, Integer.parseInt(tokens[i]));
            }
        }
        inHypernyms.close();
        
        reversedGraph = graph.reverse();

        
        
        // *** Parse SynsetCounts ***
		In inCounts = new In(synsetCounts);
		while (inCounts.hasNextLine())
		{
			String line = inCounts.readLine();
			String[] tokens = line.split(",");
			int synsetID = Integer.parseInt(tokens[0]);
			int count = Integer.parseInt(tokens[1]);
			
            // TODO: you should probably do something here
			synsetSet.get(synsetID).synsetCount = count;
			//synsetSet.get(synsetID).aggFrequency = getAggregatedFrequencyC(synsetID, new boolean[graph.V()]);	
			//synsetSet.get(synsetID).aggFrequency = getAggregatedFrequencyC(synsetID);
			//marked = new boolean[82075];
		}
		inCounts.close();

        // TODO: Remember to remove this when your constructor is done!
    	//throw new UnsupportedOperationException();
		//rootAgg = (double) synsetSet.get(0).aggFrequency;
		
		//synsetSet.get(synsetID).aggFrequency = getAggregatedFrequencyC(synsetID, new boolean[graph.V()]);
		
		
		
		for(int i = 0; i < graph.V(); i++)
		{
			synsetSet.get(i).aggFrequency = getAggregatedFrequencyC(i, new boolean[graph.V()]);
		}
		
		
		rootAgg = (double) synsetSet.get(0).aggFrequency;
		
    }

	// returns all WordNet nouns
    public Iterable<String> nouns()
    {
    	/*ArrayList<String> list = new ArrayList<String>();
    	
    	for(Synset synset: synsetSet)
    	{
    		for(String noun : synset.nouns)
    		{
    			list.add(noun);
    		}
    	}
    	return list;*/
    	return nounsAll.keySet();
    }

	// is the word a WordNet noun?
    public boolean isNoun(String word)
    {
    	//ArrayList<String> list = (ArrayList<String>) nouns();
    	return nounsAll.get(word) != null;
    }
    
	// Returns the aggregated frequency of the synset 
    public int getAggregatedFrequency(int synsetID)
	{
    	//return getAggregatedFrequencyC(synsetID, new boolean[graph.V()]);	
    	return synsetSet.get(synsetID).aggFrequency;
	}
    
    private int getAggregatedFrequencyC(int synsetID, boolean[] marked)
    {
    	marked[synsetID] = true;
    	int count =  synsetSet.get(synsetID).synsetCount;
    	Iterable<Integer> itb = reversedGraph.adj(synsetID);
    	Iterator<Integer> itr = itb.iterator();
    	
    	while(itr.hasNext())
    	{
    		
    		int index = itr.next();
    		
    		if(!marked[index])
    		{
    			count += getAggregatedFrequencyC(index, marked);
    		}
    		
    	}
    	
    	return count;
    	
    }
    
    
    
	// Returns the similarity score between the two nouns
	public double getSimilarity(String noun1, String noun2)
	{
		if(!isNoun(noun1) || !isNoun(noun2))
		{
			throw new IllegalArgumentException();
		}
		
    	HashSet<Integer> noun1ID = new HashSet<Integer>();
    	HashSet<Integer> noun2ID = new HashSet<Integer>();
    	
    	
    	double max = Double.NEGATIVE_INFINITY;
    	
    	for(Synset synset: synsetSet)
    	{
        	if(synset.nouns.contains(noun1))
    		{
    			noun1ID.add(synset.id);
    		}
    		if(synset.nouns.contains(noun2))
    		{
    			noun2ID.add(synset.id);
    		}
    	}
    	
    	
    	for(int id: noun1ID)
    	{
    		//Old
    		/*ArrayList<Integer> here = new ArrayList<Integer>(); 
    		here.add(id);
    		ArrayList<Integer> listNoun1 = allIDs(id, here);*/
    		
    		
    		//New
    		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    		map.put(id, 1);
    		map = allIDMap(id, map);
    		
    		for(int id2: noun2ID)
    		{
    			//Old 
    			/*ArrayList<Integer> allAncestors = new ArrayList<Integer>();
    			allAncestors.add(id2);
    			ArrayList<Integer> listNoun2 = allIDs(id2, allAncestors);
    			double f = (double) smallestA(allCommonAncestors(listNoun1, listNoun2));*/
    			
    			
    			//New
    			HashMap<Integer, Integer> map2 = new HashMap<Integer, Integer>();
    			
    			
    			map2.put(id2, 1);
    			
        		
        		map2 = allIDMap(id2, map2);
        		//map2 = allIDMap2(id2, map2,map);
        		
        		double f = (double) smallestAMap(allCommonMap(map, map2));
        		//double f = (double) smallestAMap(map2);
    			
    			
        		//Common
        		double num = -1 * Math.log(f/rootAgg);
        		//System.out.println(num);
        		
        		if(num >= max)
        		{
        			max = num;
        		}
    		}
    	}
        //System.out.println((System.currentTimeMillis()-startTime)+" getSimilarity time ");

    	return max;
	}
	
	
	private HashMap<Integer, Integer> allCommonMap(HashMap<Integer, Integer> noun1, HashMap<Integer, Integer> noun2)
	{
		//long start = System.currentTimeMillis();
		HashMap<Integer, Integer> common = new HashMap<Integer, Integer>();
		
		Set<Integer> noun2IDs = noun2.keySet();
		Iterator<Integer> itr = noun2IDs.iterator();
		
		while(itr.hasNext())
		{
			int id = itr.next();
			if(noun1.get(id) != null)
			{
				common.put(id, 1);
			}
		}
		//System.out.println((System.currentTimeMillis()-start)+ " allCommonMap time");
		return common;
	}
	
	
	private Integer smallestAMap(HashMap<Integer, Integer> map)
	{
		int min = Integer.MAX_VALUE;
		Set<Integer> ids = map.keySet();
		Iterator<Integer> itr = ids.iterator();
		
		while(itr.hasNext())
		{
			int id = getAggregatedFrequency(itr.next());
			if(id < min)
			{
				min = id;
			}
		}
		
		return min;
	}
	
	
	private HashMap<Integer, Integer> allIDMap(int id, HashMap<Integer, Integer> map)
	{
		
		//long start = System.currentTimeMillis();
		Iterable<Integer> itb = graph.adj(id);
		Iterator<Integer> itr = itb.iterator();
		
		while(itr.hasNext())
		{
			int hypernym = itr.next();
			if(map.get(hypernym) == null)
			{
				map.put(hypernym, 1);
			}
			map = allIDMap(hypernym, map);
		}
		//System.out.println((System.currentTimeMillis()-start)+ " allIDMap time");

		return map;
	}
	
	private HashMap<Integer, Integer> allIDMap2(int id, HashMap<Integer, Integer> map, HashMap<Integer, Integer> reference)
	{
		
		//long start = System.currentTimeMillis();
		Iterable<Integer> itb = graph.adj(id);
		Iterator<Integer> itr = itb.iterator();
		
		
		while(itr.hasNext())
		{
			int hypernym = itr.next();
			
			if(map.get(hypernym) == null && reference.get(hypernym) != null)
			{
				//System.out.println("Putting "+hypernym);
				map.put(hypernym, 1);
			}
			map = allIDMap2(hypernym, map,reference);
		}
		//System.out.println((System.currentTimeMillis()-start)+ " allIDMap time");

		return map;
	}
	
	
	
    // for unit testing of this class
    public static void main(String[] args)
    {
		/*String synsetsFile = "testInput/synsets.txt";
		String hypernymsFile = "testInput/hypernyms.txt";
		String synsetCountsFile = "testInput/SynsetCounts.txt";
		
		WordNet wordnet = new WordNet(synsetsFile, hypernymsFile, synsetCountsFile);
		

		// Add additional testing code here
		Iterable<String> test = wordnet.nouns();*/
		
		/*String one = "testInput/synsets-8.txt";
		String two = "testInput/hypernyms-8-ModTree.txt";
		String three = "testInput/SynsetCounts-8.txt";*/
		String one = "testInput/synsets.txt";
		String two = "testInput/hypernyms.txt";
		String three = "testInput/SynsetCounts.txt";
		
		
		
		WordNet name = new WordNet(one, two, three); 
		//System.out.println(name.getAggregatedFrequency(5));
		
		/*for(int i = 0; i < 8; i++)
		{
			double f = name.getAggregatedFrequency(i);
			//double num = -1 * Math.log(f/188.0);
			System.out.println(f);
		}*/
		
		System.out.println();
		System.out.println("similarity: " + name.getSimilarity("zona_pellucida", "tarsal"));
		
    }
}