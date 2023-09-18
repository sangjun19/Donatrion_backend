package com.example.Donation.controller;

import com.example.Donation.model.*;
import com.example.Donation.repository.DonateeRepository;
import com.example.Donation.repository.DonatorRepository;
import com.example.Donation.repository.LetterRepository;
import com.example.Donation.repository.BenefRepository;
import com.example.Donation.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class DonationController {

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    private BenefRepository benefRepository;

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private DonateeRepository donateeRepository;

    // 후원자(Donator) 관련 엔드포인트

    @GetMapping("/donators")
    public List<Donator> getAllDonators() {
        return donatorRepository.findAll();
    }

    @GetMapping("/donators/{id}")
    public Donator getDonatorById(@PathVariable Long id) {
        return donatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donator not found with id " + id));
    }

    @PostMapping("/donators")
    public Donator createDonator(@RequestBody Donator donator) {
        return donatorRepository.save(donator);
    }

    @PutMapping("/donators/{id}")
    public Donator updateDonator(@PathVariable Long id, @RequestBody Donator donatorDetails) {
        Donator donator = donatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donator not found with id " + id));

        donator.setName(donatorDetails.getName());
        //donator.setEmail(donatorDetails.getEmail());

        return donatorRepository.save(donator);
    }

    @DeleteMapping("/donators/{id}")
    public void deleteDonator(@PathVariable Long id) {
        Donator donator = donatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donator not found with id " + id));

        donatorRepository.delete(donator);
    }

    @PostConstruct
    void init() {
        Letter letter = new Letter();
        letter.setTitle("9월");
        letter.setContent("편지");
        letter.setDate("2023년 9월 12일");
        List<Letter> letters = new ArrayList<>();
        letters.add(letter);
        letterRepository.save(letter);
        Letter letter2 = new Letter();
        letter2.setTitle("10월");
        letter2.setContent("안녕");
        letter2.setDate("2023년 10월 12일");
        letters.add(letter2);
        letterRepository.save(letter2);

        Donatee donatee = new Donatee();
        donatee.setName("gildong");
        donatee.setMoney(1000);
        donatee.setDate("2023년 9월 12일");

        donatee.setSendLetters(letters);
        donateeRepository.save(donatee);

        Donator donator = new Donator();
        donator.setName("Hong");
        List<Donatee> donatees = new ArrayList<>();
        donatees.add(donatee);
        donator.setDonatedTo(donatees);
        donatorRepository.save(donator);

        Benef benef = new Benef();
        benef.setName("asdf");
        benef.setTitle("축구가 하고 싶어요");
        benef.setInfo("축구선수가 되는게 꿈 이에요");
        benef.setPer(80);
        benef.addHashtag("#축구");
        benef.addHashtag2("#축구선수");
        benefRepository.save(benef);

        Benef benef2 = new Benef();
        benef2.setName("qwer");
        benef2.setTitle("피아노가 하고 싶어요");
        benef2.setInfo("아름다운 피아노를 연주하고 싶어요");
        benef2.setPer(60);
        benef2.addHashtag("#피아노");
        benef2.addHashtag2("#피아니스트");
        benefRepository.save(benef2);

        //FirebaseService firebaseService = new FirebaseService();
        //firebaseService.uploadData(benef);
        //firebaseService.uploadData(benef2);
    }

    @GetMapping("/donators/{id}/donatees")
    public List<Donatee> getAllDonatees() {
        return donateeRepository.findAll();
    }

    @PostMapping("/donators/{id}/donatees")
    public Donatee createDonatee(@RequestBody Donatee donatee) {
        return donateeRepository.save(donatee);
    }

    @GetMapping("/donators/{donator_id}/donatees/{donatee_id}")
    public List<Letter> getAllLetters() {
        return letterRepository.findAll();
    }

    @PostMapping("/donators/{donator_id}/donatees/{donatee_id}")
    public Letter createLetter(@RequestBody Letter letter) {
        return letterRepository.save(letter);
    }

    @GetMapping("/benefs")
    public ResponseEntity<List<Benef>> getFilteredBeneficiaries(@RequestParam(value = "keyword") String keyword) {
        List<Benef> benefs = benefRepository.findByInfoContaining(keyword);

        if (!benefs.isEmpty()) {
            return ResponseEntity.ok(benefs);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/benefs/{id}/updatePer")
    public ResponseEntity<Benef> updateBeneficiaryPer(@PathVariable Long id, @RequestParam int per) {
        Benef beneficiary = benefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id " + id));

        // Update the 'per' field
        beneficiary.setPer(per);

        Benef updatedBeneficiary = benefRepository.save(beneficiary);
        return ResponseEntity.ok(updatedBeneficiary);
    }

    @PostMapping("/benefs")
    public Benef createBeneficiary(@RequestBody Benef benef) {
        return benefRepository.save(benef);
    }

    @PutMapping("/benefs/{id}")
    public ResponseEntity<Benef> updateBeneficiary(@PathVariable Long id, @RequestBody Benef benefDetails) {
        Benef beneficiary = benefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id " + id));

        // Update the 'per' field if it is present in the request body
//        if (benefDetails.getPer() != null) {
//            beneficiary.setPer(benefDetails.getPer());
//        }

        // You can similarly update other fields if needed

        Benef updatedBeneficiary = benefRepository.save(beneficiary);
        return ResponseEntity.ok(updatedBeneficiary);
    }


    @DeleteMapping("/benefs/{id}")
    public void deleteBeneficiary(@PathVariable Long id) {
        Benef beneficiary = benefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id " + id));

        benefRepository.delete(beneficiary);
    }
}
