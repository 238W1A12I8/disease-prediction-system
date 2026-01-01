package com.example.diseaseprediction.config;

import com.example.diseaseprediction.model.Disease;
import com.example.diseaseprediction.model.Role;
import com.example.diseaseprediction.model.Symptom;
import com.example.diseaseprediction.model.User;
import com.example.diseaseprediction.repository.DiseaseRepository;
import com.example.diseaseprediction.repository.SymptomRepository;
import com.example.diseaseprediction.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               DiseaseRepository diseaseRepository,
                               SymptomRepository symptomRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User("Admin", "admin@demo.com", passwordEncoder.encode("admin123"), Role.ADMIN);
                userRepository.save(admin);
                User user = new User("Demo User", "user@demo.com", passwordEncoder.encode("password"), Role.USER);
                userRepository.save(user);
            }

            if (diseaseRepository.count() == 0) {
                diseaseRepository.saveAll(List.of(
                        // Respiratory Diseases
                        new Disease("Flu (Influenza)", "Influenza is a viral infection that attacks your respiratory system.", 
                            "Rest, drink fluids, take antiviral medications if prescribed. Avoid contact with others."),
                        new Disease("Common Cold", "A viral infectious disease of the upper respiratory tract.", 
                            "Rest, stay hydrated, use over-the-counter cold medications. Usually resolves in 7-10 days."),
                        new Disease("COVID-19", "Coronavirus disease caused by SARS-CoV-2 virus.", 
                            "Isolate immediately, wear a mask, consult healthcare provider. Monitor oxygen levels."),
                        new Disease("Pneumonia", "Infection that inflames air sacs in one or both lungs.", 
                            "Seek medical attention immediately. May require antibiotics or hospitalization."),
                        new Disease("Bronchitis", "Inflammation of the lining of bronchial tubes.", 
                            "Rest, drink fluids, use humidifier. Avoid smoking and air pollutants."),
                        new Disease("Asthma", "Chronic condition affecting airways in the lungs.", 
                            "Use prescribed inhalers, avoid triggers, have an action plan ready."),
                        new Disease("Tuberculosis", "Bacterial infection primarily affecting the lungs.", 
                            "Requires long-term antibiotic treatment. Highly contagious - isolate and seek medical care."),
                        
                        // Neurological Conditions
                        new Disease("Migraine", "A neurological condition causing severe headaches.", 
                            "Rest in a dark, quiet room. Take prescribed medications. Stay hydrated."),
                        new Disease("Tension Headache", "Most common type of headache, feels like a band around the head.", 
                            "Over-the-counter pain relievers, stress management, adequate sleep."),
                        new Disease("Vertigo", "Sensation of spinning or dizziness.", 
                            "Sit or lie down immediately. Avoid sudden movements. See ENT specialist."),
                        
                        // Gastrointestinal Diseases
                        new Disease("Gastroenteritis", "Inflammation of the stomach and intestines.", 
                            "Stay hydrated, eat bland foods, rest. Seek care if symptoms persist over 48 hours."),
                        new Disease("Food Poisoning", "Illness caused by eating contaminated food.", 
                            "Stay hydrated, rest. Seek medical care if severe vomiting or bloody stools."),
                        new Disease("Acid Reflux (GERD)", "Chronic digestive disease where stomach acid flows back.", 
                            "Avoid trigger foods, eat smaller meals, don't lie down after eating."),
                        new Disease("Irritable Bowel Syndrome", "Chronic condition affecting the large intestine.", 
                            "Dietary changes, stress management, fiber supplements may help."),
                        
                        // Infectious Diseases
                        new Disease("Malaria", "Mosquito-borne disease caused by parasites.", 
                            "Seek immediate medical treatment. Antimalarial drugs required. Prevention is key."),
                        new Disease("Dengue Fever", "Mosquito-borne viral infection.", 
                            "No specific treatment. Rest, hydrate, take pain relievers (avoid aspirin)."),
                        new Disease("Typhoid", "Bacterial infection spread through contaminated food/water.", 
                            "Antibiotics required. Hospitalization may be necessary for severe cases."),
                        new Disease("Chickenpox", "Highly contagious viral infection causing itchy rash.", 
                            "Calamine lotion for itching, antihistamines, stay hydrated. Isolate from others."),
                        
                        // Skin Conditions
                        new Disease("Eczema", "Condition that makes skin red, inflamed, and itchy.", 
                            "Moisturize regularly, avoid triggers, use prescribed creams."),
                        new Disease("Psoriasis", "Autoimmune condition causing rapid skin cell buildup.", 
                            "Topical treatments, light therapy, medications as prescribed."),
                        new Disease("Urticaria (Hives)", "Skin reaction causing itchy welts.", 
                            "Antihistamines, avoid known triggers, cool compresses."),
                        
                        // Cardiovascular
                        new Disease("Hypertension", "High blood pressure condition.", 
                            "Lifestyle changes, reduced salt intake, regular exercise, medications if prescribed."),
                        new Disease("Anemia", "Condition where blood lacks enough healthy red blood cells.", 
                            "Iron supplements, dietary changes, treat underlying cause."),
                        
                        // Other Common Conditions
                        new Disease("Diabetes (Type 2)", "Chronic condition affecting blood sugar regulation.", 
                            "Diet management, regular exercise, blood sugar monitoring, medications."),
                        new Disease("Allergic Rhinitis", "Allergic response causing sneezing, itchy eyes, runny nose.", 
                            "Antihistamines, nasal sprays, avoid allergens."),
                        new Disease("Conjunctivitis (Pink Eye)", "Inflammation of the eye's outer membrane.", 
                            "Warm compresses, eye drops. Bacterial cases need antibiotic drops."),
                        new Disease("Sinusitis", "Inflammation of the sinuses.", 
                            "Nasal decongestants, saline rinses, rest. Antibiotics if bacterial."),
                        new Disease("Tonsillitis", "Inflammation of the tonsils.", 
                            "Rest, warm liquids, pain relievers. May need antibiotics if bacterial."),
                        new Disease("Ear Infection (Otitis Media)", "Infection of the middle ear.", 
                            "Pain relievers, warm compresses. Antibiotics may be prescribed.")
                ));
            }

            if (symptomRepository.count() == 0) {
                symptomRepository.saveAll(List.of(
                        // General Symptoms
                        new Symptom("fever"),
                        new Symptom("high fever"),
                        new Symptom("mild fever"),
                        new Symptom("chills"),
                        new Symptom("fatigue"),
                        new Symptom("weakness"),
                        new Symptom("body aches"),
                        new Symptom("muscle pain"),
                        new Symptom("joint pain"),
                        new Symptom("sweating"),
                        new Symptom("night sweats"),
                        new Symptom("weight loss"),
                        new Symptom("loss of appetite"),
                        
                        // Respiratory Symptoms
                        new Symptom("cough"),
                        new Symptom("dry cough"),
                        new Symptom("productive cough"),
                        new Symptom("sore throat"),
                        new Symptom("runny nose"),
                        new Symptom("nasal congestion"),
                        new Symptom("sneezing"),
                        new Symptom("shortness of breath"),
                        new Symptom("breathing difficulty"),
                        new Symptom("wheezing"),
                        new Symptom("chest pain"),
                        new Symptom("chest tightness"),
                        
                        // Head & Neurological
                        new Symptom("headache"),
                        new Symptom("severe headache"),
                        new Symptom("throbbing headache"),
                        new Symptom("dizziness"),
                        new Symptom("lightheadedness"),
                        new Symptom("vertigo"),
                        new Symptom("confusion"),
                        new Symptom("sensitivity to light"),
                        new Symptom("sensitivity to sound"),
                        
                        // Gastrointestinal
                        new Symptom("nausea"),
                        new Symptom("vomiting"),
                        new Symptom("diarrhea"),
                        new Symptom("constipation"),
                        new Symptom("abdominal pain"),
                        new Symptom("stomach cramps"),
                        new Symptom("bloating"),
                        new Symptom("heartburn"),
                        new Symptom("loss of taste"),
                        new Symptom("loss of smell"),
                        
                        // Skin Symptoms
                        new Symptom("rash"),
                        new Symptom("itchy skin"),
                        new Symptom("red spots"),
                        new Symptom("blisters"),
                        new Symptom("skin peeling"),
                        new Symptom("hives"),
                        new Symptom("swelling"),
                        
                        // Eye & Ear
                        new Symptom("red eyes"),
                        new Symptom("watery eyes"),
                        new Symptom("itchy eyes"),
                        new Symptom("eye discharge"),
                        new Symptom("ear pain"),
                        new Symptom("hearing loss"),
                        new Symptom("ringing in ears"),
                        
                        // Other
                        new Symptom("swollen lymph nodes"),
                        new Symptom("sinus pressure"),
                        new Symptom("difficulty swallowing"),
                        new Symptom("frequent urination"),
                        new Symptom("excessive thirst"),
                        new Symptom("rapid heartbeat"),
                        new Symptom("pale skin")
                ));
            }
        };
    }
}
