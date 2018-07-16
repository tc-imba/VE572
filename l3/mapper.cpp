//
// Created by liu on 18-7-5.
//

#include <iostream>
#include <string>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    string str;
    getline(cin, str);

    while (getline(cin, str) && !str.empty()) {
        auto p1 = str.find(',');
        auto p2 = str.find(',', p1 + 1);
        cout << str.substr(p1 + 1, p2 - p1 - 1) << '\t' << str.substr(p2 + 1) << '\n';
    }

    return 0;
}

