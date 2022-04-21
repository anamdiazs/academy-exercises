package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
	
	int totalUsers = 0;
	int totalReviews = 0;
	int totalProducts = 0;
	Hashtable<String, Integer> users = new Hashtable<String, Integer>();
	Hashtable<String, Integer> products = new Hashtable<String, Integer>();
	Hashtable<Integer, String> productsById =new Hashtable<Integer, String>();


	public MovieRecommender (String fileUrl) throws IOException {
        GZIPInputStream document = new GZIPInputStream(new FileInputStream(fileUrl));
        BufferedReader bReader = new BufferedReader(new InputStreamReader(document));
        String line;
        File result = new File("movies.csv");
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(result));

        String productListCSV = "";
        String userListCSV = "";
        String scoreListCSV = "";

        int numProducts = 0;
        int numUsers = 0;
        int numReviews = 0;

        while ((line = bReader.readLine()) != null){
            if (line.startsWith("product/productId:")){
                numReviews++;
                String [] tempLine = line.split(" ");
                String productId = tempLine[1];
                if (!products.containsKey(productId)){
                    products.put(productId, numProducts);
                    productsById.put(numProducts,productId);
                    numProducts++;
                }
                productListCSV = products.get(productId).toString();

            } else if (line.startsWith("review/userId:")) {
                
                String [] tempLine = line.split(" ");
                String userID = tempLine[1];
                if (!users.containsKey(userID)){
                    users.put(userID,numUsers++);
                }
                userListCSV = users.get(userID).toString();


            } else if (line.startsWith("review/score:")){
                String [] tempLine = line.split(" ");
                scoreListCSV = tempLine[1];
                bWriter.write(userListCSV + "," + productListCSV + "," + scoreListCSV + "\n");
            }
        }

        bReader.close();
        bWriter.close();

        this.totalReviews = numReviews;
        this.totalProducts = products.size();
        this.totalUsers = users.size();

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
		List<String> results = new ArrayList<String>();

		DataModel model = new FileDataModel(new File("movies.csv"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

		List<RecommendedItem> allRecommendations = recommender.recommend(users.get(userId), 3);
		for (RecommendedItem recommendation : allRecommendations ) {
			results.add(productsById.get((int)recommendation.getItemID()));
		}

		return results;
	}

}
