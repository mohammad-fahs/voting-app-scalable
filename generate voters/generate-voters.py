import json
import random

# Settings
num_files = 6
records_per_file = 5000

# Create 6 JSON files in the same folder as the script
for file_num in range(1, num_files + 1):
    records = []
    
    # Add 30,000 generated records for each file
    for i in range(records_per_file):
        generated_name = f"GeneratedName{file_num}_{i+1}"
        generated_nationalId = f"B{file_num}{str(i+1).zfill(8)}"  # Like B100000001
        records.append({
            "name": generated_name,
            "nationalId": generated_nationalId
        })
    
    # Shuffle the records (optional)
    random.shuffle(records)

    # Save to JSON file in the same folder as the script
    file_path = f"voters_{file_num}.json"
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(records, f, ensure_ascii=False, indent=4)

print("✅ JSON files created successfully in the current directory!")
