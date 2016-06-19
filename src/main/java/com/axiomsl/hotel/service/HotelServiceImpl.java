package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Hotel;
import com.axiomsl.hotel.model.Hotel_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Hotel Service implementation
 */
@Service("hotelService")
@Repository
@Transactional
public class HotelServiceImpl implements HotelService {
    private HotelRepository hotelRepository;

    @Autowired
    public void setHotelRepository(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional(readOnly=true)
    public List<Hotel> findAllByCriteria(String name, String city, String state) {
        return hotelRepository.findAll(Specifications.where(getSpecification(name, city, state)));
    }

    /* Generate criteria query */
    private Specification<Hotel> getSpecification(final String name, final String city, final String state) {
        return new Specification<Hotel>() {
            @Override
            public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                root.fetch(Hotel_.rooms, JoinType.LEFT);
                Predicate predicate = criteriaBuilder.conjunction();
                if (name != null && !name.isEmpty()) {
                    Predicate p = criteriaBuilder.equal(root.get(Hotel_.name),
                            name);
                    predicate = criteriaBuilder.and(predicate, p);
                }
                if (city != null && !city.isEmpty()) {
                    Predicate p = criteriaBuilder.equal(root.get(Hotel_.city),
                            city);
                    predicate = criteriaBuilder.and(predicate, p);
                }
                if (state != null && !state.isEmpty()) {
                    Predicate p = criteriaBuilder.equal(root.get(Hotel_.state),
                            state);
                    predicate = criteriaBuilder.and(predicate, p);
                }
                return predicate;
            }
        };
    }

    @Override
    public Hotel findOne(Long id) {
        return hotelRepository.findOne(id);
    }

    @Override
    public Hotel findByName(String name) {
        return hotelRepository.findByName(name);
    }

    @Override
    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Override
    public void delete(Long id) {
        hotelRepository.delete(id);
    }
}
