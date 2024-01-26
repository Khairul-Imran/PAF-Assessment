package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	To insert after fixing small issue.
	 */
	public List<String> getSuburbs(String country) {
		return template.findDistinct(new Query(), "address.suburb", "listings", String.class);
	}

	/*
	db.listings.aggregate([
    { $match : {
        "address.suburb" : { $regex : "bondi beach", $options : "i" },
        accommodates : { $gte : 2 },
        min_nights : { $gte : 5 },
        price : { $lte : 500 }
    }},
    {
        $project : { _id: 1, name : 1, accommodates : 1, price: 1 }
    },
    {
        $sort : { price: -1 }
    }
	])
	 */
	public List<AccommodationSummary> findListings(String suburb, int accommodatesGuests, int minNights, float maxPrice) {

		MatchOperation matchListings = Aggregation.match(Criteria
			.where("address.suburb").regex(suburb, "i")
			.and("accommodates").is(accommodatesGuests)
			.and("min_nights").gte(minNights)
			.and("price").lte(maxPrice)
			);

		ProjectionOperation projectListingFields = Aggregation.project("_id", "name", "accommodates", "price");

		SortOperation sortAccordingToPrice = Aggregation.sort(Sort.Direction.ASC, "price");

		Aggregation pipeline = Aggregation.newAggregation(matchListings, projectListingFields, sortAccordingToPrice);

		AggregationResults<Document> results = template.aggregate(pipeline, "listings", Document.class);

		List<AccommodationSummary> listOfAccommodations = new LinkedList<>();
		
		for (Document document: results) {
			AccommodationSummary accommodationSummary = new AccommodationSummary();
            String _id = document.getString("_id");
            String name = document.getString("name");
			int accommodates = document.getInteger("accommodates");
			float price = document.get("price", Number.class).floatValue();

			accommodationSummary.setId(_id);
			accommodationSummary.setName(name);
			accommodationSummary.setAccomodates(accommodates);
			accommodationSummary.setPrice(price);

            listOfAccommodations.add(accommodationSummary);
         }

		 return listOfAccommodations;
	}


	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
