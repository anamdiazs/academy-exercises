package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


class MovieRecommender {
	String filePath;
	int totalUsers = 0;
	int totalReviews = 0;
	int totalProducts = 0;
	Hashtable<String, Integer> users = new Hashtable<String, Integer>();
	Hashtable<String, Integer> products = new Hashtable<String, Integer>();
	Hashtable<Integer, String> productsById =new Hashtable<Integer, String>();


	MovieRecommender (String fileUrl) throws IOException {
		filePath = fileUrl;
        GZIPInputStream document = new GZIPInputStream(new FileInputStream(filePath));
        BufferedReader bReader = new BufferedReader(new InputStreamReader(document));
        String line;
        File result = new File("movies.csv");
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(result));

	}
	public void decompressFile () throws IOException {
    String DECOMPRESSED_FILE = "./movieRecommendations.txt";
	 MovieRecommender gzip;
		try {
			System.out.print("Inside try catch");
			gzip = new MovieRecommender("movies.txt.gz");
			gzip.deCompressGZipFile("movies.txt.gz", DECOMPRESSED_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
	
  public void deCompressGZipFile(String gzipFile, String destFile) {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    GZIPInputStream gZIPInputStream = null;
    try {
		System.out.print("Inside second try catch");
      fis = new FileInputStream(gzipFile);
      gZIPInputStream = new GZIPInputStream(fis);
      fos = new FileOutputStream(destFile);
      byte[] buffer = new byte[1024];
      int len;
      while((len = gZIPInputStream.read(buffer)) > 0){
        fos.write(buffer, 0, len);
      }
    }catch (IOException e) {
      e.printStackTrace();
    }finally {
      try {
        if(gZIPInputStream != null) {				
          gZIPInputStream.close();
        }
        if(fos != null) {				
          fos.close();					
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

	
	public int getTotalUsers() {
		return this.totalUsers;
	}

	public int getTotalReviews() {
		return this.totalReviews;
	}

	public int getTotalProducts() {
		return this.totalProducts;
	}


	public List<String> getRecommendations(String userId) throws IOException, TasteException {
		DataModel model = new FileDataModel(new File("movies.csv"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

		List<String> recommendations = new ArrayList<String>();
		for (RecommendedItem recommendation : recommender.recommend(users.get(userId), 3)) {
			recommendations.add(productsById.get((int) (recommendation.getItemID())));
		}

		return recommendations;
	}

}
