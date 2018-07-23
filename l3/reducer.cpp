//
// Created by liu on 18-7-5.
//

#include <iostream>
#include <string>
#include <unordered_map>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    unordered_map<size_t, size_t> m;
    size_t id, score;

    while (cin >> id >> score) {
        auto it = m.find(id);
        if (it == m.end()) {
            m.emplace_hint(it, id, score);
        } else if (it->second < score) {
            it->second = score;
        }
    }

    for (const auto &item : m) {
        cout << item.first << '\t' << item.second << '\n';
    }

    return 0;
}

