import dayjs from "dayjs";
import duration from "dayjs/plugin/duration";

dayjs.extend(duration);
export const utils = {
  formatTimestampToYYYMMDDHHMMSS(timestamp) {
    return dayjs(timestamp).format("YYYY-MM-DD HH:mm:ss");
  },
  formatTimestampToYYYMMDD(timestamp) {
    return dayjs(timestamp).format("YYYY-MM-DD");
  },
  timeStampToMinutes(timestamp) {
    return dayjs.duration(timestamp).asMinutes();
  },
  verifyEmailFormat(email) {
    if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(email)) {
      return false;
    }
    return true;
  },
  urbanConversion(date) {
    return dayjs(date).format("YYYY-MM-DDTHH:mm:ss");
  },
  getTimeForAnyWeek(index = 0) {
    if (index == 0) {
      return dayjs();
    } else {
      return dayjs().subtract(index, "week");
    }
  },
  getTimeForAnyMonth(index = 0) {
    if (index == 0) {
      return dayjs();
    } else {
      return dayjs().subtract(index, "month");
    }
  },
  getsmall() {
    return window.innerWidth < 768 ? true : false;
  },
  passwordUtil(str){
    return str.replace(/[\w\s]/gi,'*')
  },
  nanUtil(num){
    if(isNaN(num)){
      return 0;
    }else{
      return num;
    }
  },
  getProxyUrl(url){
    return '/javaSever'+url
  }
};
