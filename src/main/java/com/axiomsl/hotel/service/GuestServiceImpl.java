package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Guest Service implementation
 */
@Repository
@Transactional
@Service("guestService")
public class GuestServiceImpl implements GuestService {
    private GuestRepository guestRepository;

    @Autowired
    public void setGuestRepository(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Guest findByLogin(String login) {
        return guestRepository.findByLogin(login);
    }

    @Override
    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }

    @Override
    public void delete(Long id) {
        guestRepository.delete(id);
    }
}
