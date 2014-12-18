private void computeSavePCA() throws IOException {
    	int d = getVectorDimension(cache);
        int[] shape = {d,d};
        INDArray cov = Nd4j.create(shape);
        for(int i = 0; i < 2; i++) {
        	assert cov.size(i) == d : "covariance size neds to be d";
        }
        System.out.println("computing covariance");
        Iterator<String> words_it = cache.words().iterator();
        while(words_it.hasNext()) {
        	String word = words_it.next();
        	INDArray a = vec.getWordVectorMatrixNormalized(word);
        	if(a.isRowVector()) {
        		a = a.transpose();
        	}
        	INDArray addum = a.mmul(a.transpose());
        	for(int i = 0; i < 2; i++) {
            	assert addum.size(i) == d : "addum size neds to be d";
            }
        	cov.addi(addum);
        }
        for(int i = 0; i < 2; i++) {
        	assert cov.size(i) == d : "covariance size neds to be d";
        }
        printSizes(cov, "cov", 'd', 'd');
//        Nd4j.writeTxt(cov, "cov.csv", ",");
        System.out.println("performing pca");
        int k = 2;
        INDArray proj = PCA.pca(cov, k, true);
//        Nd4j.writeTxt(proj, "proj.csv", ",");
    	assert proj.size(0) == d : " d incorrect";
    	assert proj.size(1) == k : " k incorrect";
    	// since this returns a complex matrix, get the real component only
   		plot(getRealComponent(proj), vec, cache.words());
    }
    
    /**
     * Get the dimension of a vector in the vocabulary
     * @param cache
     * @return the dim
     */
    private int getVectorDimension(VocabCache cache)
    {
    	System.out.println("getting vector dimension...");
    	int d = -1;
    	Iterator<INDArray> vec_it = cache.vectors();
        if(vec_it.hasNext()) {
        	INDArray a = vec_it.next();
        	assert a.isVector() : "Vocab word vector isn't a vector";
        	d = a.size(0);
        }
        return d;
    }
    private static final int LIMIT = 1000;
    /**
     * Plot projection
     * @param proj dxk projection matrix
     * @param data dxn data matrix
     * @param labels
     * @throws IOException
     */
    public void plot(INDArray proj, Word2Vec vec, Collection<String> words) throws IOException {
    	proj = proj.transpose();
    	int k = proj.size(0), d = proj.size(1);
        BufferedWriter write = new BufferedWriter(new FileWriter(new File("wordspca.csv"),true));
        Iterator<String> wordIter = words.iterator();
        int n = 0, mod = (int) Math.sqrt(words.size());
        while(wordIter.hasNext()) {
        	if(n > LIMIT) break;
            String word = wordIter.next();
            if(word == null)
                continue;
            StringBuffer sb = new StringBuffer();
        	INDArray data = vec.getWordVectorMatrixNormalized(word);
        	data = data.transpose();
        	INDArray result = proj.mmul(data);
        	result = result.transpose();
            for(int j = 0; j < k; j++) {
            	double val = result.getDouble(0,j);
                sb.append(val);
                if(j < k - 1)
                    sb.append(",");
            }

            sb.append(",");
            sb.append(word);
            sb.append(" ");

            sb.append("\n");
            write.write(sb.toString());

            n++;
        }

        write.flush();	
        write.close();
    }
    public INDArray getRealComponent(INDArray m) {
    	if(m instanceof IComplexNDArray) {
    		return ((IComplexNDArray) m).real();
    	}
    	else return m;
    }
    private void printSizes(INDArray m, String name, char one, char two) {
    	System.out.printf("%s:: %c:%d x %c:%d\n", name, one, m.size(0), two, m.size(1));
    }
