
    
    private void computeAccuracyOnTest(Word2Vec vec, String testFileName) {
    	try {
    		File testFile = new File(testFileName);
    		Scanner s = new Scanner(testFile);
    		int qid = 0, lineNumber = 0; // qid section number
    		int ccn = 0, tcn = 0,
    			cacn = 0, tacn = 0,
    			seac = 0, secn = 0,
    			syac = 0, sycn = 0;
    		int tq = 0, tqs = 0;
    		while(s.hasNextLine()) {
    			lineNumber++;
    			if(tqs > 3) {
    				break;
    			}
    			String line = s.nextLine();
    			String[] toks = line.split("\\s");
    			if(toks[0].equals(":")) {
    				// this indicates the start of a new section
    				// its label follows
    				// print its label, and stats so far
    				System.out.println("new section:");
    				if(line.length() > 1) {
    					System.out.println(line.substring(1));	
    				}
    				if(qid > 0) {
    					System.out.printf("Accuracy top1: %.2f %% (%d / %d)\n",
    							ccn / ((double) tcn) * 100, ccn, tcn);
    					System.out.printf("Total accuracy: %.2f %% "
    							+ "Semantic Accuracy: %.2f %% "
    							+ "Syntactic Accuracy: %.2f %%\n",
    							cacn / ((double) tacn) * 100,
    							seac / ((double) secn) * 100,
    							syac / ((double) sycn) * 100);
    				}
    				qid++;
    			}
    			else { // else a question line in the given section
    				if(toks.length != 4) {
    					System.err.println("possible error in file format? line:"+lineNumber);
    					continue;
    				}
    				// uppercase all strings
    				for(int i = 0; i < toks.length; i++) {
    					toks[i] = toks[i].toUpperCase();
    				}
    				// check first 3 strings are in vocab
    				for(int i = 0; i < 3; i++) {
    					if(!vec.hasWord(toks[i])) {
    						continue;
    					}
    				}
    				tq++;
    				// check fourth string
    				if(!vec.hasWord(toks[3])) {
						continue;
					}
    				tqs++;
    				// vectors: A - B + C = ? 
    				// go through vocab, find closest among words
    				// that aren't A, B, or C
    				double bestDist = 0, currDist = 0;
    				String bestWord = "-1";
    				Iterator<INDArray> vecs = cache.vectors();
    				INDArray[] toks_vecs = new INDArray[4];
    				for(int i = 0; i < 4; i++) {
    					toks_vecs[i] = vec.getWordVectorMatrix(toks[i]);
    				}
    				INDArray comp = toks_vecs[0].subi(toks_vecs[1]).add(toks_vecs[2]);
    				int index = 0;
    				while(vecs.hasNext()) {
    					String currWord = cache.wordAtIndex(index);
    					boolean toSkip = false;
    					for(int i = 0; i < 3; i++) {
    						if(currWord.toUpperCase().equals(toks[i])) {
    							toSkip = true;
    						}
    					}
    					if(toSkip) continue;
    					INDArray curr = vecs.next();
    					currDist = comp.distance2(curr);
    					if(currDist > bestDist) {
    						bestWord = cache.wordAtIndex(index);
    						bestDist = currDist;
    					}
    					index++;	
    				}
    				// edit! apparently a method does this? hopefully?
//    				System.out.println(analogyWords.toString());
//    				TreeSet<VocabWord> tr = vec.analogy(toks[0], toks[1], toks[2]);
//    				System.out.println(tr.toString());
        			// TODO does this work?
//    				bestWord = vec.analogyWords(toks[0], toks[1], toks[2]).get(0);
    				if(bestWord.toUpperCase().equals(toks[3])) {
    					ccn++;
    					cacn++;
    					if(qid <= 5) {
    						seac++;
    					}
    					else{
    						syac++;
    					}
    				}
    				if(qid <= 5) {
    					secn++;
    				}
    				else {
    					sycn++;
    				}
    				tcn++;	
    				tacn++;
    			}
    		}
    		System.out.printf("Questions seen / total: %d %d  %.2f %%\n",
    				tqs, tq, tqs/ ((double) tq) * 100);
    	}
    	catch(IOException ex) {
    		ex.printStackTrace();
    	}
    }
