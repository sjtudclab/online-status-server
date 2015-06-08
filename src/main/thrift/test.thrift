namespace java cn.edu.sjtu.se.dclab.oss.thrift

service OnlineStatusQueryService {
    string checkOnline(1:i64 userId)
}