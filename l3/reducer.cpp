//
// Created by liu on 18-7-5.
//

#include <iostream>
#include <string>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    size_t id, score;
    size_t result = 0;

    while (cin >> id >> score) {
        result = max(result, score);
    }

    cout << id << '\t' << result << '\n';

    return 0;
}

