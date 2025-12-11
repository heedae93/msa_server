# 🐶 강아지 감정 분석 AI 서버 (Dog Emotion Analysis)

FastAPI와 YOLOv8을 사용하여 강아지의 표정을 분석하고 감정(Happy, Angry, Sad, Relaxed)을 알려주는 마이크로서비스입니다.

## 📂 프로젝트 구조
- `main.py`: FastAPI 서버 메인 코드
- `train.py`: AI 모델 학습용 스크립트
- `dog_emotion_project/.../best.pt`: 학습된 모델(best.pt)이 저장된 위치
- `requirements.txt`: 필요 라이브러리 목록

## 🚀 실행 방법 (How to Run)

1. **가상환경 생성 및 활성화**
```
   python -m venv .venv
   .\.venv\Scripts\activate
```
2. **패키지 설치**
```
   pip install -r requirements.txt
```
3. **서버 실행**
```
   uvicorn main:app --reload
```
4. **테스트**
    - 웹 브라우저 접속: http://127.0.0.1:8000/docs
    - `POST /analyze` API에 강아지 사진 업로드

## 🛠️ 학습 방법 (Training)
데이터셋을 준비한 후 아래 명령어로 재학습할 수 있습니다.
```
python train.py
```