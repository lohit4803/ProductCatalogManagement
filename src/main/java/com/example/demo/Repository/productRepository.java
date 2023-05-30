package com.example.demo.Repository;
import com.example.demo.Models.categoryModel;
import com.example.demo.Models.productModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface productRepository extends JpaRepository<productModel, Long> {

}
