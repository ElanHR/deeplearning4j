
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
    				// initialize vector components: A - B + C
    				tqs++;
    				// go through vocab, find closest among words
    				// that aren't A, B, or C
//    				System.out.print("words:");
//    				for(int i = 0; i < toks.length; i++) {
//    					if(i == 3) System.out.print('(');
//    					System.out.print(toks[i]);
//    					if(i == 3) System.out.print(')');
//    					if(i < 3) System.out.print(',');
//    				}
//    				System.out.println();
    				// edit! apparently a method does this? hopefully?
//    				System.out.println(analogyWords.toString());
//    				TreeSet<VocabWord> tr = vec.analogy(toks[0], toks[1], toks[2]);
//    				System.out.println(tr.toString());
    				List<String> analogyWords = vec.analogyWords(toks[0], toks[1], toks[2]);
    				// TODO does this work?
    				String answer = analogyWords.get(0);
    				if(answer.toUpperCase().equals(toks[3])) {
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
