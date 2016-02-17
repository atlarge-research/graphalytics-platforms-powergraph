#pragma once
#include <chrono>

#ifdef GRANULA

namespace granula {
    using namespace std;

    class operation {
        public:
            string operationUuid;
            string actor_type;
            string actor_id;
            string mission_type;
            string mission_id;

            operation(string a_type, string a_id, string m_type, string m_id) {
                operationUuid = generateUuid();
                actor_type = a_type;
                actor_id = a_id;
                mission_type = m_type;
                mission_id = m_id;
            }

            string generateUuid() {
                long uuid;
                if (sizeof(int) < sizeof(long))
                uuid = (static_cast<long>(rand()) << (sizeof(int) * 8)) | rand();
                return to_string(uuid);
            }

            string getOperationInfo(string infoName, string infoValue) {
                return "GRANULA - OperationUuid:" + operationUuid + " " +
                   "ActorType:" + actor_type + " " +
                   "ActorId:" + actor_id + " " +
                   "MissionType:" + mission_type + " " +
                   "MissionId:" + mission_id + " " +
                   "InfoName:" + infoName + " " +
                   "InfoValue:" + infoValue + " " +
                   "Timestamp:" + getEpoch();
            }

            string getEpoch() {
                return to_string(chrono::duration_cast<chrono::milliseconds>
                    (chrono::system_clock::now().time_since_epoch()).count());
            }
    };
}

#endif
