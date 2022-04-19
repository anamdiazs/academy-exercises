package nearsoft.academy.bigdata.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
	int totalUsers;
	int totalReviews;
	int totalProducts;
	Hashtable<String, Integer> users;
	Hashtable<String, Integer> products;
	Hashtable<Integer, String> productsById;

	MovieRecommender(String fileUrl) throws IOException {
		filePath = fileUrl;
		users = new Hashtable<String, Integer>();
		products = new Hashtable<String, Integer>();
		productsById = new Hashtable<Integer, String>();
		totalUsers = 0;
		totalReviews = 0;
		totalProducts = 0;

	}

	public List<String> getRecommendations(String userId) throws IOException, TasteException {
		DataModel model = new FileDataModel(new File("moviesRecommendation.csv"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

		List<String> recommendations = new ArrayList<String>();
		for (RecommendedItem recommendation : recommender.recommend(users.get(userId), 3)) {
			recommendations.add(productsById.get((int) (recommendation.getItemID())));
		}

		return recommendations;
	}

	public int getTotalUsers() {
		return totalUsers;
	}

	public int getTotalReviews() {
		return totalReviews;
	}

	public int getTotalProducts() {
		return totalProducts;
	}
}