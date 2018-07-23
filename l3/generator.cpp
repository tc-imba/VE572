//
// Created by liu on 18-7-16.
//

#include <string>
#include <vector>
#include <unordered_set>
#include <fstream>
#include <iostream>

using namespace std;

int main(int argc, char *argv[]) {
    const size_t NAMES_SIZE = argc > 1 ? strtoul(argv[1], nullptr, 10) : 10000;
    const size_t DATA_SZIE = NAMES_SIZE * 50;

    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    ifstream firstNamesFile("firstnames.txt");
    ifstream lastNamesFile("lastnames.txt");
    vector<string> firstNames;
    vector<string> lastNames;
    string temp;
    while (firstNamesFile >> temp && !temp.empty()) {
        firstNames.emplace_back(move(temp));
    }
    while (lastNamesFile >> temp && !temp.empty()) {
        lastNames.emplace_back(move(temp));
    }
    firstNamesFile.close();
    lastNamesFile.close();

    cout << "firstNames: " << firstNames.size() << endl;
    cout << "lastNames: " << lastNames.size() << endl;

    cout << "Generating names";
    unordered_set<string> namesSet;
    vector<string> names;
    vector<size_t> ids;
    namesSet.reserve(NAMES_SIZE);
    names.reserve(NAMES_SIZE);
    while (namesSet.size() < NAMES_SIZE) {
        size_t a = firstNames.size() * rand() / RAND_MAX;
        size_t b = lastNames.size() * rand() / RAND_MAX;
        string name = firstNames[a] + " " + lastNames[b];
        auto it = namesSet.find(name);
        if (it == namesSet.end()) {
            namesSet.emplace_hint(it, name);
            names.emplace_back(move(name));
            size_t id = 1000000000 + (size_t) 1000000000 * rand() / RAND_MAX;
            ids.emplace_back(id);
        }
        if (namesSet.size() % (NAMES_SIZE / 50) == 0) {
            cout << ".";
            cout.flush();
        }
    }
    cout << endl;

    cout << "Generating grades";
    ofstream grades("grades.csv");
    //grades << "name,ID,grade\n";
    for (size_t i = 0; i < DATA_SZIE; i++) {
        size_t index = NAMES_SIZE * rand() / RAND_MAX;
        size_t score = (size_t) 101 * rand() / RAND_MAX;
        grades << names[index] << "," << ids[index] << "," << score << "\n";
        if (i % (DATA_SZIE / 50) == 0) {
            cout << ".";
            cout.flush();
        }
    }
    grades.close();
    cout << endl;

    return 0;
}