//
// Created by liu on 18-7-5.
//

#include <iostream>
#include <string>
#include <unordered_map>

using namespace std;

int main() {
    unordered_map<size_t, size_t> m;
    while (true) {
        size_t id, score;
        cin >> id >> score;
        if (!cin) break;
        auto it = m.find(id);
        if (it == m.end()) {
            m.emplace_hint(it, id, score);
        } else if (it->second < score) {
            it->second = score;
        }
    }

    for (const auto &item : m) {
        cout << item.first << '\t' << item.second << endl;
    }

    return 0;
}

