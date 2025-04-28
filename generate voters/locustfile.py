from locust import HttpUser, TaskSet, task, between, events
import random
import json
import requests

# Global variables
voters = []
option_ids = []

voter_index = 0

# Load voters once before starting
for i in range(1, 2):  # you have voters_1.json to voters_6.json
    with open(f"voters_{i}.json", "r", encoding="utf-8") as f:
        voters.extend(json.load(f))

@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """
    Fetch option IDs from the /options endpoint when the test starts.
    """
    global option_ids
    options_url = "http://voting.local/options"
    try:
        response = requests.get(options_url)
        response.raise_for_status()
        options = response.json()
        option_ids = [option['id'] for option in options]
        print(f"✅ Retrieved {len(option_ids)} options.")
    except Exception as e:
        print(f"❌ Failed to fetch options from {options_url}: {e}")
        option_ids = []  # fallback to empty, so that test fails clearly if necessary

class VotingTasks(TaskSet):
    @task
    def vote(self):
        global voter_index

        if not option_ids:
            print("❗ No options available, skipping vote.")
            return
        
        if voter_index >= len(voters):
            print("✅ All voters have voted. Skipping further votes.")
            return

        voter = voters[voter_index]
        voter_index += 1

        option_id = random.choice(option_ids)  # Still picking a random option
        national_id = voter["nationalId"]

        self.client.post(
            f"/vote?optionId={option_id}&nationalId={national_id}"
        )

class VotingUser(HttpUser):
    tasks = [VotingTasks]
    wait_time = between(0.1, 1)  # Random wait between tasks
    host = "http://voting.local"
