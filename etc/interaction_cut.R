# 2021 1학기 산학프로젝트
# interactions 데이터 가공 2021-05-23


# working directory를 script의 위치로 자동으로 설정
library(rstudioapi)
current_path <- getActiveDocumentContext()$path
if (getwd() != current_path){
    setwd(dirname(current_path))
}

library(readxl)

# 음식 명 리스트 불러오기
foodList <- read_excel("food2.xls")
foodNameVector <- foodList[["식품명"]]
foodNumVector <- foodList[["NO"]]

set.seed(1000)
random_idx = sample(x=1:length(foodList),size=1000,replace=T)
original_data = read.csv("Interactions_data.csv")
for(i in 1:1000){
    original_data[["USER_ID"]][i] = floor(i/5) + 1
    original_data[["ITEM_ID"]][i] = foodNumVector[random_idx[i]] 
}

write.csv(original_data, "Num_Interactions_data.csv")
    