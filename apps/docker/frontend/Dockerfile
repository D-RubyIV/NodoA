FROM node:18-alpine AS build
WORKDIR /apps/frontend
COPY ./apps/frontend .
RUN npm install
EXPOSE 8085
CMD ["npm", "run", "serve"]
