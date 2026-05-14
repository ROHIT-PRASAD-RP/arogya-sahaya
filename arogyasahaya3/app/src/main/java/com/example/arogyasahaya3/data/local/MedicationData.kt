package com.example.arogyasahaya3.data.local

data class MedicationRecommendation(
    val condition: String,
    val medicineName: String,
    val dosage: String,
    val timing: String,
    val notes: String
)

object MedicationData {
    val recommendations = listOf(
        MedicationRecommendation(
            condition = "Hypertension",
            medicineName = "Lisinopril",
            dosage = "5mg - 10mg once daily",
            timing = "Morning",
            notes = "Common ACE inhibitor. Watch for dry cough."
        ),
        MedicationRecommendation(
            condition = "Hypertension",
            medicineName = "Amlodipine",
            dosage = "2.5mg - 5mg once daily",
            timing = "Any time",
            notes = "Calcium channel blocker. May cause leg swelling."
        ),
        MedicationRecommendation(
            condition = "Diabetes",
            medicineName = "Metformin",
            dosage = "500mg once or twice daily",
            timing = "With meals",
            notes = "First-line therapy. Reduces stomach upset when taken with food."
        ),
        MedicationRecommendation(
            condition = "Diabetes",
            medicineName = "Sitagliptin (Januvia)",
            dosage = "25mg - 100mg once daily",
            timing = "Any time",
            notes = "DPP-4 inhibitor. Low risk of hypoglycemia."
        ),
        MedicationRecommendation(
            condition = "Asthma",
            medicineName = "Albuterol (Ventolin)",
            dosage = "1-2 puffs every 4-6 hours",
            timing = "As needed",
            notes = "Rescue inhaler for sudden shortness of breath."
        ),
        MedicationRecommendation(
            condition = "Asthma",
            medicineName = "Fluticasone (Flovent)",
            dosage = "1-2 puffs twice daily",
            timing = "Morning & Evening",
            notes = "Preventive steroid. Rinse mouth after use."
        ),
        MedicationRecommendation(
            condition = "Cholesterol",
            medicineName = "Atorvastatin (Lipitor)",
            dosage = "10mg - 20mg once daily",
            timing = "Night/Any time",
            notes = "Moderate intensity statin for heart health."
        ),
        MedicationRecommendation(
            condition = "Hypothyroidism",
            medicineName = "Levothyroxine",
            dosage = "12.5mcg - 25mcg once daily",
            timing = "Empty stomach",
            notes = "Take 30-60 mins before breakfast with water only."
        )
    )
}
