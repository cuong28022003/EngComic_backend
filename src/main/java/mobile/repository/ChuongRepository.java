package mobile.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import mobile.model.Entity.*;;

public interface ChuongRepository extends MongoRepository<Chuong, String> {
}
